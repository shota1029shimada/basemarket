// 認証（ログイン）専用コントローラー
package com.basemarket.controller;

import com.basemarket.dto.request.RegisterRequest;
import com.basemarket.service.AuthService;
import jakarta.validation.Valid;

import com.basemarket.dto.request.LoginRequest;
import com.basemarket.dto.response.LoginResponse;
import com.basemarket.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	// Spring Security の認証処理を担当するクラス
	private final AuthenticationManager authenticationManager;

	//
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

}

	// ログイン処理
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(
			@RequestBody LoginRequest request,
			HttpServletResponse response) {

		// email と password を Spring Security に渡すためのトークンを作成
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				request.getEmail(),
				request.getPassword());

		//  認証実行（ここで CustomUserDetailsService が呼ばれる）
		Authentication authentication = authenticationManager.authenticate(authToken);

		// 認証成功 → SecurityContext に保存
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// JWTを生成（subject = email）
		String jwt = jwtUtil.generateToken(authentication.getName());

		// JWTを HttpOnly Cookie に保存
		Cookie cookie = new Cookie("JWT_TOKEN", jwt);
		cookie.setHttpOnly(true); // JavaScript から触れない（XSS対策）
		cookie.setSecure(false); // 本番では true（HTTPS必須）
		cookie.setPath("/"); // 全APIに送信
		cookie.setMaxAge(24 * 60 * 60); // 24時間

		response.addCookie(cookie);

		// フロントに返すレスポンス（トークン自体は返さない）
		return ResponseEntity.ok(
				new LoginResponse("Login successful"));
	}

	// ログアウト処理
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response) {

		// JWT Cookie を削除（MaxAge=0）
		Cookie cookie = new Cookie("JWT_TOKEN", null);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		cookie.setPath("/");
		cookie.setMaxAge(0);

		response.addCookie(cookie);

		return ResponseEntity.ok().build();
	}
}
