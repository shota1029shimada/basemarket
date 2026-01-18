// Spring Securityと 認証・認可のルール設定
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				// CSRF（JWT+Cookieなら有効推奨）
				.csrf(csrf -> csrf.disable()) // 最初は無効でもOK

				// セッション使わない
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// フォームログイン無効
				.formLogin(form -> form.disable())
				.httpBasic(basic -> basic.disable())
				// 認可設定
				.authorizeHttpRequests(auth -> auth

						// 認証不要(画面=未ログインでも見れる)
						.requestMatchers(
								"/",
								"/error", // ★これが重要
								"/login",
								"/register",
								"/auth/**",
								"/items/**",
								"/ranking",
								"/css/**",
								"/js/**",
								"/images/**",
								"/webjars/**",
								"/favicon.ico")
						.permitAll()
						// 静的ファイル
						.requestMatchers(
								"/css/**",
								"/js/**",
								"/images/**",
								"/webjars/**")
						.permitAll()

						// 管理者専用
						.requestMatchers("/admin/**")
						.hasRole("ADMIN")

						// 認証API（ログイン/登録/ログアウト）
						.requestMatchers("/auth/**").permitAll()

						// その他はログイン必須
						.anyRequest()
						.authenticated())

				// JWTフィルター追加
				.addFilterBefore(
						jwtFilter,
						UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	AuthenticationManager authenticationManager(
			AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
