package com.basemarket.dto.response;

import java.time.LocalDateTime;

import com.basemarket.entity.Users;

import lombok.Getter;

/**
 * ユーザー情報を返却するレスポンスDTO
 */
@Getter
public class UserResponse {

	private Long id;
	private String username;
	private String email;
	private boolean isBanned;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	/**
	 * Entity → Response変換コンストラクタ
	 * @param user Usersエンティティ
	 */
	public UserResponse(Users user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.isBanned = user.isBanned();
		this.createdAt = user.getCreatedAt();
		this.updatedAt = user.getUpdatedAt();
	}
}
