//Spring Security 全体の設定クラス
//Spring Securityと 認証・認可のルール設定＋どのURLが認証必要か、不要かを定義
package com.basemarket.config;

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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.basemarket.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	// JWTを検証する自作フィルター
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	/**
	 * 🔐 セキュリティ全体の設定
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				// CSRF対策を無効化（JWTを使うため）
				.csrf(csrf -> csrf.disable())

				// セッションを使わない（JWTなので stateless）
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// URLごとのアクセス制御
				.authorizeHttpRequests(auth -> auth
						// 認証不要
						.requestMatchers(
								"/auth/login",
								"/auth/register",
								"/items",
								"/items/**")
						.permitAll()

						// 管理者のみ
						.requestMatchers("/admin/**").hasRole("ADMIN")

						// それ以外はログイン必須
						.anyRequest().authenticated())

				// JWTフィルターを Spring Security に組み込む
				.addFilterBefore(
						jwtAuthenticationFilter,
						UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * 🔑 パスワード暗号化（BCrypt）
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		// passwordHash を安全に保存するため
		return new BCryptPasswordEncoder();
	}

	/**
	 * 🔐 AuthenticationManager
	 * ログイン処理で使用される
	 */
	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
