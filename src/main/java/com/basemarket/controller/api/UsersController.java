package com.basemarket.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basemarket.dto.request.UpdateUserRequest;
import com.basemarket.dto.response.UserResponse;
import com.basemarket.entity.Users;
import com.basemarket.service.UsersService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

	private final UsersService usersService;

	/**
	 * ログイン中のユーザー情報取得
	 * GET /api/users/me
	 */
	@GetMapping("/me")
	public ResponseEntity<UserResponse> getMyProfile() {
		Users user = usersService.getLoginUser();
		return ResponseEntity.ok(new UserResponse(user));
	}

	/**
	 * ユーザー情報更新
	 * PUT /api/users/me
	 */
	@PutMapping("/me")
	public ResponseEntity<UserResponse> updateMyProfile(
			@RequestBody UpdateUserRequest request) {

		Users updatedUser = usersService.updateUser(request);
		return ResponseEntity.ok(new UserResponse(updatedUser));
	}

	/**
	 * 管理者向け：全ユーザー一覧取得
	 * GET /api/users
	 */
	@GetMapping
	public ResponseEntity<?> getAllUsers() {
		return ResponseEntity.ok(usersService.getAllUsers()
				.stream()
				.map(UserResponse::new)
				.toList());
	}
}
