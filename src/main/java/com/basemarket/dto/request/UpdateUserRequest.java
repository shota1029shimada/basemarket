package com.basemarket.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * ユーザー情報更新用リクエストDTO
 * JWTで認証されたログインユーザーが自分の情報を更新するために使用
 */
@Getter
@Setter
public class UpdateUserRequest {

	@NotBlank(message = "ユーザー名は必須です")
	private String username;

	@Email(message = "正しいメール形式で入力してください")
	@NotBlank(message = "メールアドレスは必須です")
	private String email;

	// パスワードは空でもOK（変更しない場合）
	private String password;
}
