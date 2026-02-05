//認証結果の利用
//srole 判定・管理者チェック・ログイン有無チェック
package com.basemarket.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.basemarket.entity.Users;
import com.basemarket.exception.ResourceNotFoundException;
import com.basemarket.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

	private final UsersRepository usersRepository;

	/**
	 * 現在ログイン中のユーザーを取得
	 *
	 * @return Users ログインユーザー
	 */
	public Users getLoginUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResourceNotFoundException("ログインユーザーが存在しません");
		}

		String email = authentication.getName();

		return usersRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("ユーザーが存在しません"));
	}

	/**
	 * 現在ログイン中のユーザーが管理者かどうかを判定
	 *
	 * @return boolean 管理者の場合true
	 */
	public boolean isAdmin() {
		try {
			Users loginUser = getLoginUser();
			String role = loginUser.getRole();
			// "ADMIN" または "ROLE_ADMIN" を管理者として判定
			return "ADMIN".equals(role) || "ROLE_ADMIN".equals(role);
		} catch (Exception e) {
			return false;
		}
	}
}
