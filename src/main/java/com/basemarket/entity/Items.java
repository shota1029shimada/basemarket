package com.basemarket.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Items {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "items_id")
	private Long itemsId;

	@Column(name = "sellers_id", nullable = false)
	private Long sellersId;

	@Column(name = "categories_id", nullable = false)
	private Long categoriesId;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Column(name = "description", nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(name = "price", nullable = false)
	private Integer price;

	@Column(name = "condition", nullable = false, length = 20)
	private String condition;

	@Column(name = "status", nullable = false, length = 20)
	private String status = "AVAILABLE";

	@Column(name = "view_count", nullable = false)
	private Integer viewCount = 0;

	@Column(name = "like_count", nullable = false)
	private Integer likeCount = 0;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "sold_at")
	private LocalDateTime soldAt;
}