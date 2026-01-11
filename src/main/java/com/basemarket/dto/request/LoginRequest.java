// ログインリクエスト
//email, password
package com.basemarket.dto.request;

import lombok.Data;

@Data
public class LoginRequest {

	private String email;
	private String password;

}
