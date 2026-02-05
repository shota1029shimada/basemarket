package com.basemarket.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.basemarket.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class ModelAttributeAdvice {

	private final SecurityUtil securityUtil;

	/**
	 * すべてのコントローラーのリクエストでログイン状態をModelに追加
	 * 
	 * @return boolean ログインしている場合true
	 */
	@ModelAttribute("isLoggedIn")
	public boolean isLoggedIn() {
		try {
			securityUtil.getLoginUser();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
