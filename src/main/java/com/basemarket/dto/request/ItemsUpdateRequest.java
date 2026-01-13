//商品編集用リクエストDTO(商品「作成」と「編集」は責務が異なるためDTOを分離)
package com.basemarket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/*
 * ・出品者IDはJWTログインユーザーから取得するため含めない
 */
@Getter
@Setter
public class ItemsUpdateRequest {

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
