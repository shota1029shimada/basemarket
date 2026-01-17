package com.basemarket.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.basemarket.dto.request.UpdateUserRequest;
import com.basemarket.entity.Users;
import com.basemarket.repository.UsersRepository;
import com.basemarket.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {

	private final UsersRepository usersRepository;
	private final SecurityUtil securityUtil;
	private final PasswordEncoder passwordEncoder;

	/**
	 * JWTからログインユーザーを取得
	 */
	public Users getLoginUser() {
		return securityUtil.getLoginUser();
	}

	/**
	 * ユーザー情報更新
	 */
	public Users updateUser(UpdateUserRequest request) {
		Users user = getLoginUser();

		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());

		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		}

		return usersRepository.save(user);
	}

	/**
	 * 管理者用：全ユーザー取得
	 */
	public List<Users> getAllUsers() {
		return usersRepository.findAll();
	}
}
