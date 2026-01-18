//ログインレスポンス・ jwtToken, username
package com.basemarket.dto.response;

import lombok.Getter;

@Getter
public class LoginResponse {

	private String message;

	public LoginResponse(String message) {
		this.message = message;
	}
}