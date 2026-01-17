package com.basemarket.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * Likes 用リクエストDTO
 * ・itemId: 対象商品ID
 */
@Getter
@Setter
public class LikesRequest {

	@NotNull(message = "商品IDは必須です")
	private Long itemId;
}
