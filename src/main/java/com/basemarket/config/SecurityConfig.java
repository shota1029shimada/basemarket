// Spring Securityと 認証・認可のルール設定
package com.basemarket.config;

// static import：HttpMethod.GET / POST などを「GET.name()」みたいに短く書くため
import static org.springframework.http.HttpMethod.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.basemarket.security.JwtAuthenticationFilter;

@Configuration
// このクラスを「設定クラス」としてSpringに認識させる（@Beanメソッドが有効になる）
// → 起動時にSpringがこのクラスを読み取り、Beanを生成する

@EnableWebSecurity
// Spring Securityを有効化するアノテーション
// → セキュリティフィルター（認証・認可の仕組み）をSpring Bootに組み込む
public class SecurityConfig {

	// JWT認証を行うフィルター（自作クラス）をDIで受け取る
	// → すべてのリクエストに対して「Cookie/ヘッダのJWTを見てログイン状態を作る」役割
	private final JwtAuthenticationFilter jwtFilter;

	//コンストラクタインジェクション（推奨）
	// → Springが JwtAuthenticationFilter を自動で注入してくれる
	public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
	//SecurityFilterChain：Spring Securityの「ルールセット」本体をBeanとして登録する
	// → ここで「どのURLが認証不要か」「どのURLがログイン必須か」などを定義する
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				// CSRF対策（Cross Site Request Forgery）
				// 通常は「フォームPOST」などで必要になる
				// 今回は「JWTで認証し、サーバー側セッションを持たない（STATELESS）」構成のため無効化している
				// ※ただし JWT を Cookie に保存している場合、設計によってはCSRFが必要になるケースもあるので注意
				.csrf(csrf -> csrf.disable())

				// セッションを使わない（＝ログイン状態をサーバーに保持しない）
				// → 毎リクエストごとにJWTを確認して認証する思想
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// pring Securityのデフォルト機能（フォームログイン）を無効化
				// → /login 画面やUsernamePasswordAuthenticationFilterによる認証の仕組みを使わない設計にする
				.formLogin(form -> form
						.loginPage("/login")
						.loginProcessingUrl("/login") // POST /login を Security が処理
						.defaultSuccessUrl("/items", true) // 成功時
						.failureUrl("/login?error") // 失敗時
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login")
						.permitAll())

				//Basic認証（Authorization: Basic ...）も使わない
				.httpBasic(basic -> basic.disable())

				//認可設定（Authorization）
				// 「このURLにアクセスできるのは誰か」を決める
				.authorizeHttpRequests(auth -> auth

						// ===== 認証不要（未ログインOK）=====
						// permitAll()：誰でもアクセスOK
						.requestMatchers(
								"/", // トップ
								"/error", //エラー時にログインへ飛ばず、原因が見えるようになる
								"/login", // ログイン画面
								"/register", // 登録画面
								"/css/*", // 静的ファイル
								"/js/*",
								"/auth/*", // 認証関連のエンドポイント（例: /auth/login 等）
								"/images/*")
						.permitAll()

						// ===== 認証必須 =====
						// authenticated()：ログインしているユーザーのみOK
						.requestMatchers("/items/new").permitAll()
						.requestMatchers(
								"/items/*/edit",
								"/items/*/delete")
						.authenticated()

						// ✅ 「商品一覧・商品詳細」は GET のときだけ誰でもOK
						// AntPathRequestMatcher(path, method) で HTTPメソッドも条件に入れられる
						.requestMatchers(new AntPathRequestMatcher("/items", GET.name()))
						.permitAll()
						.requestMatchers(new AntPathRequestMatcher("/items/*", GET.name()))
						.permitAll()

						// ===== ログイン必須（ページ）=====
						// ✅ /items への POST（出品登録など）はログイン必須
						.requestMatchers(new AntPathRequestMatcher("/items", POST.name()))
						.authenticated() // 出品POST

						// ===== API（ログイン必須）=====
						// ✅ /api/* は全部ログイン必須にしている
						// → フロントからのAjax/JSON APIを想定
						.requestMatchers("/api/*").authenticated()

						// ===== 管理者 =====
						// hasRole("ADMIN")：ROLE_ADMIN を持つユーザーのみ許可
						// ※Spring Securityでは内部的に "ROLE_" が付いた権限名で判定されることが多い
						.requestMatchers("/admin/*").hasRole("ADMIN")

						// ✅ ここ、上で "/auth/*" permitAll() が既にあるので重複している（機能的には問題ないが整理可能）
						.requestMatchers("/auth/*").permitAll()

						// 上記以外は全部ログイン必須
						.anyRequest().authenticated())

				//  未ログイン時の挙動（認証例外が起きた時）
				// authenticationEntryPoint：未認証で保護URLにアクセスした際の「入口処理」
				// LoginUrlAuthenticationEntryPoint("/login")：ブラウザアクセスなら /login にリダイレクトしてくれる
				// ※API呼び出し（JSON）だと「リダイレクト」より「401を返す」方が自然な場合も多い（今後調整ポイント）
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(
								new LoginUrlAuthenticationEntryPoint("/login")))

				// JWTフィルターをセキュリティチェーンに差し込む
				// addFilterBefore(A, B)：Bが実行される前にAを実行する
				// UsernamePasswordAuthenticationFilter：本来フォームログインの認証を担当するフィルタ
				// → その前に jwtFilter を動かして「JWTから認証情報を作る」ことで、後続の認可判定が通るようにする

				.addFilterBefore(
						jwtFilter,
						UsernamePasswordAuthenticationFilter.class);

		// 上記設定をビルドしてSecurityFilterChainとして返す
		return http.build();
	}

	@Bean
	//  AuthenticationManager：認証処理の中心（ユーザー名・パスワード照合など）を実行する部品
	// → AuthServiceなどで「ログイン処理」を行うときに使うことが多い
	AuthenticationManager authenticationManager(
			AuthenticationConfiguration config) throws Exception {
		// ✅ Springが用意するAuthenticationManagerを取得して返す
		return config.getAuthenticationManager();
	}

	@Bean
	// PasswordEncoder：パスワードのハッシュ化/照合を行う部品
	// BCryptPasswordEncoder：強力なハッシュ関数（一般的に推奨）
	// → ユーザー登録時に平文をハッシュ化して保存
	// → ログイン時に入力パスワードとDBのハッシュを照合
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
