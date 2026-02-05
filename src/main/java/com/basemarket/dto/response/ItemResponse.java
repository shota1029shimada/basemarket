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
	private Boolean isBookmarked; // ブックマーク状態（null = 未ログインまたは未判定）

	// Entity → Response 変換（seller/category が null でも NPE を防ぐ）
	public ItemResponse(Items item) {
		this.id = item.getId();
		this.title = item.getTitle();
		this.description = item.getDescription();
		this.price = item.getPrice();
		this.condition = item.getCondition();

		this.sellerId = item.getSeller() != null ? item.getSeller().getId() : null;
		this.sellerName = item.getSeller() != null ? item.getSeller().getUsername() : null;

		this.categoryId = item.getCategory() != null ? item.getCategory().getId() : null;
		this.categoryName = item.getCategory() != null ? item.getCategory().getCategoryName() : null;

		this.viewsCount = item.getViewsCount() != null ? item.getViewsCount() : 0;
		this.createdAt = item.getCreatedAt();
		this.isBookmarked = null; // デフォルトはnull（後で設定可能）
	}

	// ブックマーク状態を設定するメソッド
	public void setBookmarked(boolean bookmarked) {
		this.isBookmarked = bookmarked;
	}
}
