package com.basemarket.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemImages {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_images_id")
	private Long itemImagesId;

	@Column(name = "items_id", nullable = false)
	private Long itemsId;

	@Column(name = "image_url", nullable = false, length = 500)
	private String imageUrl;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}