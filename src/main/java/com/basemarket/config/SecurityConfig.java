// Spring Securityと 認証・認可のルール設定
package com.basemarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
				// B案：とにかく CRUD 完成を優先するため、セキュリティ機能を一旦停止
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.anyRequest().permitAll());

		// B案：JWTフィルタ・entryPoint なども一旦使わない（CRUD完成後に戻す）
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
