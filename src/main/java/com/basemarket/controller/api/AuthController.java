// 認証（ログイン）専用コントローラー
package com.basemarket.controller.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basemarket.dto.request.LoginRequest;
import com.basemarket.dto.request.RegisterRequest;
import com.basemarket.dto.response.LoginResponse;
import com.basemarket.security.JwtUtil;
import com.basemarket.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	// Spring Security の認証処理を担当するクラス
	private final AuthenticationManager authenticationManager;
	//登録処理の本体
	private final AuthService authService;
	// JWTの生成・検証を行うユーティリティ
	private final JwtUtil jwtUtil;

	//ユーザー登録API
	@PostMapping("/register")
	public ResponseEntity<?> register(
			@Valid @RequestBody RegisterRequest request) {
		authService.register(request);
		return ResponseEntity.ok("登録が完了しました");
	}

	// ログイン処理
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(
			@Valid @RequestBody LoginRequest request,
			HttpServletResponse response) {

		// email + password を Security に渡す
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				request.getEmail(),
				request.getPassword());

		// 認証（CustomUserDetailsService が呼ばれる）
		Authentication authentication = authenticationManager.authenticate(authToken);

		// 認証成功 → セキュリティコンテキストに保存
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// JWT発行
		String jwt = jwtUtil.generateToken(authentication.getName());

		// JWTを Cookie に保存
		Cookie cookie = new Cookie("JWT", jwt);
		cookie.setHttpOnly(true); // JSからアクセス不可
		cookie.setSecure(false); // 本番は true
		cookie.setPath("/");
		cookie.setMaxAge(24 * 60 * 60);

		response.addCookie(cookie);

		return ResponseEntity.ok(new LoginResponse("Login successful"));
	}

	// ログアウト
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response) {

		// Cookie削除
		Cookie cookie = new Cookie("JWT", null);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		cookie.setPath("/");
		cookie.setMaxAge(0);

		response.addCookie(cookie);

		return ResponseEntity.ok().build();
	}
}