// 新規登録リクエスト
//username, email, password
package com.basemarket.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * ユーザー登録用リクエストDTO
 * 「画面から送られてくる値」を受け取るだけ
 */
@Getter
@Setter
public class RegisterRequest {

	@NotBlank(message = "ユーザー名は必須です")
	private String username;

	@Email(message = "正しいメール形式で入力してください")
	@NotBlank(message = "メールアドレスは必須です")
	private String email;

	@NotBlank(message = "パスワードは必須です")
	private String password;
}
