//ログイン・新規登録のHTML画面を表示するだけのコントローラ
package com.basemarket.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // このクラスが「画面（HTML）を返すコントローラ」であることをSpringに伝える
// ・@RestController と違い、returnしたStringは「JSON」ではなく
//   「テンプレート名（HTML）」として扱われる
// ・主に Thymeleaf + templates 配下のHTMLを返す用途
public class AuthPageController {

	/**
	 * ログイン画面表示
	 * GET /login
	 */
	@GetMapping("/login")
	// ・URLが /login のときにこのメソッドが呼ばれる
	// 例：ブラウザで http://localhost:8080/login にアクセス
	// → このメソッドが実行される
	public String login() {
		// returnした文字列 = templatesフォルダ配下のHTML名
		// この場合：src/main/resources/templates/login.html
		// がレンダリングされてブラウザに返される
		//
		// ※ 拡張子 .html は書かない（Springが自動補完する）
		return "login";
	}

	/**
	 * ユーザー登録画面表示
	 * GET /register
	 */
	@GetMapping("/register") // /register に GET でアクセスされたときの画面表示用
	// ・ログイン前ユーザー向けのページ
	// ・SecurityConfig で permitAll() されている前提
	public String register() {
		// return "register";
		// templates/register.html を返す
		// Controller → View(HTML) を返すだけの
		// 「純粋な画面表示用コントローラ」
		return "register";
	}
}
