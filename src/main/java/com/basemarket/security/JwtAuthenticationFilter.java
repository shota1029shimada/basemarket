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

@Slf4j //Lombokが「log」というロガーを自動生成してくれるアノテーション
// → log.info(...), log.warn(...) などが使えるようになる（Loggerを自分でnewしなくていい）

@Component //SpringのDIコンテナにこのクラスを登録する（Bean化する）
// → SecurityConfig で jwtFilter としてDIできるようになる
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	//OncePerRequestFilter：1リクエストにつき1回だけ実行されるフィルターの基底クラス
	// → フィルターが2回実行される事故を防ぐ
	// → Spring Securityのフィルターチェーンに組み込みやすい

	private final JwtUtil jwtUtil;//JWTの「検証」「ユーザー名の取り出し」などを担当するユーティリティ
	private final CustomUserDetailsService userDetailsService;
	// usernameからDBなどでユーザー情報(UserDetails)を取得するサービス
	// → Spring Securityが認証/権限判定に使う形式(UserDetails)に変換して返す役目

	public JwtAuthenticationFilter(
			JwtUtil jwtUtil,
			CustomUserDetailsService userDetailsService) {
		// コンストラクタインジェクション
		// → Springが JwtUtil / CustomUserDetailsService を自動で注入する
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		// JWT取得（Cookie）
		// ブラウザが送ってきたCookieの中から "JWT" という名前のCookieを探して取り出す
		String token = null;

		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("JWT".equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}
			}
		}

		// デバッグログ（必要なら残してOK）
		log.info("[JWT] {} {} cookiePresent={}",
				request.getMethod(),
				request.getRequestURI(),
				token != null);
		if (token != null) {
			log.info("[JWT] tokenLength={}", token.length());
		}

		// ★A仕様（フォームログイン中）
		// JWTが無いリクエストでは、JWT認証処理をせず次へ（＝セッション認証を壊さない）
		if (token == null || token.isBlank()) {
			filterChain.doFilter(request, response);
			return;
		}

		// すでに認証されていない場合のみ処理
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
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

					// リクエスト詳細をセット
					authentication.setDetails(
							new WebAuthenticationDetailsSource().buildDetails(request));

					// SecurityContextへ登録
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			} catch (Exception e) {
				// JWT不正・期限切れ等は未ログイン扱いで進める
				log.warn("JWT検証失敗: {}", e.getMessage());
			}
		}

		// 次のフィルターへ
		filterChain.doFilter(request, response);
	}
}