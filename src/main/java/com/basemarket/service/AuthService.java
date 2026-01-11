//認証関連のビジネスロジック
//ユーザー登録、ログイン、JWT生成
package com.basemarket.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.basemarket.dto.request.RegisterRequest;
import com.basemarket.entity.Users;
import com.basemarket.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UsersRepository usersRepository;
	private final PasswordEncoder passwordEncoder;

	// ユーザー登録処理
	public void register(RegisterRequest request) {

		//メール重複チェック
		if (usersRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("このメールアドレスは既に登録されています");
		}

		//Usersエンティティ作成
		Users user = Users.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				// パスワードは必ずハッシュ化
				.passwordHash(passwordEncoder.encode(request.getPassword()))
				.role("ROLE_USER")
				.isBanned(false)
				.build();

		//DBに保存
		usersRepository.save(user);
	}
}
