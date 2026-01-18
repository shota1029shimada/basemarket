package com.basemarket.dto.response;

import java.time.LocalDateTime;

import com.basemarket.entity.Items;

import lombok.Getter;

@Getter
public class ItemResponse {

	private Long id;
	private String title;
	private String description;
	private Integer price;
	private String condition;

	private Long sellerId;
	private String sellerName;

	private Long categoryId;
	private String categoryName;

	private Integer viewsCount;
	private LocalDateTime createdAt;

	// Entity → Response 変換
	public ItemResponse(Items item) {
		this.id = item.getId();
		this.title = item.getTitle();
		this.description = item.getDescription();
		this.price = item.getPrice();
		this.condition = item.getCondition();

		this.sellerId = item.getSeller().getId();
		this.sellerName = item.getSeller().getUsername(); // 修正

		this.categoryId = item.getCategory().getId();
		this.categoryName = item.getCategory().getCategoryName(); // 修正

		this.viewsCount = item.getViewsCount();
		this.createdAt = item.getCreatedAt();
	}
}
