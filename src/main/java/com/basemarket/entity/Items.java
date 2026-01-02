//Itemsテーブルに対応・商品情報（タイトル、価格、説明等）
package com.basemarket.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Items {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "items_id")
	private Long id;

	// 出品者
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private Users seller;

	// カテゴリ
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categories_id", nullable = false)
	private Categories category;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private Integer price;

	// 商品状態（新品・中古など）
	@Column(nullable = false, length = 20)
	private String condition;

	// 出品状態
	@Column(nullable = false, length = 20)
	private String status; // AVAILABLE / SOLD / DELETED

	@Column(name = "views_count", nullable = false)
	private Integer viewsCount = 0;

	@Column(name = "likes_count", nullable = false)
	private Integer likesCount = 0;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "sold_at")
	private LocalDateTime soldAt;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		status = "AVAILABLE";
	}

	@PreUpdate
	public void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
