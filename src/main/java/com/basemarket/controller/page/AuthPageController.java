package com.basemarket.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPageController {

	/**
	 * ログイン画面表示
	 * GET /login
	 */
	@GetMapping("/login")
	public String login() {
		return "auth/login"; // templates/auth/login.html を返す
	}

	/**
	 * ユーザー登録画面表示
	 * GET /register
	 */
	@GetMapping("/register")
	public String register() {
		return "auth/register"; // templates/auth/register.html を返す
	}
}
