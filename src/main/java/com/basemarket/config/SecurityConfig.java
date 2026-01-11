//Spring Security 全体の設定クラス
//Spring Securityと 認証・認可のルール設定＋どのURLが認証必要か、不要かを定義
package com.basemarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * 認可ルール定義
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// CSRF 無効（API 開発中は必須）
				.csrf(csrf -> csrf.disable())
				// フォームログイン無効
				.formLogin(form -> form.disable())

				// Basic 認証無効
				.httpBasic(AbstractHttpConfigurer::disable)

				// 認可設定
				.authorizeHttpRequests(auth -> auth
						.anyRequest().permitAll());

		return http.build();
	}

	/**
	 * AuthenticationManager を Spring に登録
	 */
	@Bean
	AuthenticationManager authenticationManager(
			AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/**
	 * パスワードエンコーダ
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
