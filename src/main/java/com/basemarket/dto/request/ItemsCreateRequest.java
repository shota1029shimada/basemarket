//商品出品リクエスト
//itle, description, price, category_id等
package com.basemarket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemsCreateRequest {

	@NotBlank //文字列専用のアノテーション(nullでも、空文字列でも、空白でもあってはならない)
	private String title;

	@NotBlank
	private String description;

	@NotNull //nullであってはならない(空の文字列（""）や空のコレクションは許す)
	private Integer price;

	@NotBlank
	private String condition;

	@NotNull
	private Long categoryId;

	@NotNull
	private Long sellerId;//出品者ID → users.user_id
}
