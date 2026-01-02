//JWTフィルター。リクエストごとにトークン検証
package com.basemarket.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.basemarket.service.CustomUserDetailsService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService userDetailsService;

	/**
	 * コンストラクタインジェクション（推奨）
	 */
	public JwtAuthenticationFilter(
			JwtUtil jwtUtil,
			CustomUserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * フィルターの本体
	 *
	 * リクエストが来るたびに自動で呼ばれる
	 */
	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		String token = null;

		/**
		 * 【JWT取得】
		 * 今回の設計では「HttpOnly Cookie」からJWTを取得する
		 */
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("JWT".equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}
			}
		}

		/**
		 * トークンが存在し、まだ認証されていない場合のみ処理
		 */
		if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			// トークンが有効か検証
			if (jwtUtil.validateToken(token)) {

				// トークンからユーザー名（email）を取得
				String username = jwtUtil.getUsernameFromToken(token);

				/**
				 * UserDetails をDBから取得
				 * → CustomUserDetailsService が呼ばれる
				 */
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				/**
				 * Spring Security 用の認証オブジェクトを作成
				 *
				 * 第1引数：ユーザー情報
				 * 第2引数：パスワード（JWTなので null）
				 * 第3引数：権限（ROLE_USER など）
				 */
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities());

				// リクエスト情報をセット（IPアドレスなど）
				authentication.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request));

				/**
				 * ★最重要★
				 * ここで Spring Security に
				 * 「このリクエストはログイン済です」
				 * と教えている
				 */
				SecurityContextHolder.getContext()
						.setAuthentication(authentication);
			}
		}

		/**
		 * 次のフィルターへ処理を渡す
		 */
		filterChain.doFilter(request, response);
	}
}