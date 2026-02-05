// Spring Securityと 認証・認可のルール設定
package com.basemarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				// CSRF無効化（JWT認証を使用するため）
				.csrf(csrf -> csrf.disable())
				// セッション管理を無状態に（JWT認証のため）
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// 認可設定
				.authorizeHttpRequests(auth -> auth
						// 公開エンドポイント（認証不要）
						.requestMatchers("/", "/items", "/login", "/register",
								"/css/**", "/js/**", "/images/**", "/error").permitAll()
						// 認証（画面フォーム送信）は公開
						.requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register").permitAll()
						// 画面：出品・編集・削除確認画面はログイン必須（より具体的なパスを先に定義）
						.requestMatchers("/items/new").authenticated()
						.requestMatchers("/items/*/edit").authenticated()
						.requestMatchers("/items/*/delete").authenticated()
						.requestMatchers(HttpMethod.POST, "/items").authenticated()
						.requestMatchers(HttpMethod.PUT, "/items/*").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/items/*").authenticated()
						// 商品詳細は公開（数字のみのパス）
						.requestMatchers("/items/{id:\\d+}").permitAll()
						// API：参照(GET)のみ公開、それ以外は認証必須
						.requestMatchers(HttpMethod.GET, "/api/items", "/api/items/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/items", "/api/items/**").authenticated()
						.requestMatchers(HttpMethod.PUT, "/api/items/*").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/api/items/*").authenticated()
						// 認証が必要なAPIエンドポイント
						.requestMatchers("/api/bookmarks/**", "/api/likes/**", "/api/users/**").authenticated()
						// その他は認証必須
						.anyRequest().authenticated())
				// JWTフィルターをUsernamePasswordAuthenticationFilterの前に追加
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

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
