//Bookmarks一覧用レスポンス
package com.basemarket.dto.response;

import lombok.Getter;

@Getter
public class ActionResponse {
	private final String message;

	public ActionResponse(String message) {
		this.message = message;
	}
}
