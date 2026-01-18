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

	@NotBlank
	private String title;

	@NotBlank
	private String description;

	@NotNull
	private Integer price;

	@NotBlank
	private String condition;

	@NotNull
	private Long categoryId;
}
