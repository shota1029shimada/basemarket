//JWTフィルター。リクエストごとにトークン検証
package com.basemarket.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService userDetailsService;

	public JwtAuthenticationFilter(
			JwtUtil jwtUtil,
			CustomUserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		String token = null;

		// JWT取得（Cookie）
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("JWT".equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}
			}
		}

		// すでに認証されていない場合のみ処理
		if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			try {
				// トークン検証
				if (jwtUtil.validateToken(token)) {

					// ユーザー名取得
					String username = jwtUtil.getUsernameFromToken(token);

					// DBからユーザー取得
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);

					// 認証オブジェクト生成
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails,
							null,
							userDetails.getAuthorities());

					authentication.setDetails(
							new WebAuthenticationDetailsSource()
									.buildDetails(request));

					// SecurityContextへ登録
					SecurityContextHolder.getContext()
							.setAuthentication(authentication);
				}

			} catch (Exception e) {
				// JWT不正・期限切れ等
				log.warn("JWT検証失敗: {}", e.getMessage());
			}
		}

		// 次のフィルターへ
		filterChain.doFilter(request, response);
	}
}
