//APIではなく、認証専用の画面遷移コントローラ
package com.basemarket.controller.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.basemarket.dto.request.LoginRequest;
import com.basemarket.dto.request.RegisterRequest;
import com.basemarket.security.JwtUtil;
import com.basemarket.service.AuthService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final AuthService authService;
	private final JwtUtil jwtUtil;

	/**
	 * 新規ー登録（画面）
	 * POST /auth/register
	 */
	@PostMapping("/register")
	public String register(@Valid RegisterRequest request) {
		authService.register(request);
		return "redirect:/login"; // UC-A05
	}

	/**
	 * ログイン（画面）
	 * POST /auth/login
	 */
	@PostMapping("/login")
	public String login(
			@Valid LoginRequest request,
			HttpServletResponse response) {

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				request.getEmail(),
				request.getPassword());

		final Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(authToken);
		} catch (AuthenticationException ex) {
			// 画面ログインは 403 ではなくログイン画面へ戻す
			return "redirect:/login?error";
		}

		SecurityContextHolder.getContext()
				.setAuthentication(authentication);

		String jwt = jwtUtil.generateToken(authentication.getName());

		Cookie cookie = new Cookie("JWT", jwt);
		cookie.setHttpOnly(true);
		cookie.setSecure(false); // 本番 true
		cookie.setPath("/");
		cookie.setMaxAge(24 * 60 * 60);

		response.addCookie(cookie);

		// ログイン成功後は必ず商品一覧画面へリダイレクト
		return "redirect:/items";
	}

	/**
	 * ログアウト
	 */
	@PostMapping("/logout")
	public String logout(HttpServletResponse response) {

		Cookie cookie = new Cookie("JWT", null);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		cookie.setPath("/");
		cookie.setMaxAge(0);

		response.addCookie(cookie);

		return "redirect:/login"; // UC-A03
	}
}
