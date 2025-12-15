package com.basemarket.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "orders_id", "reviewers_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reviews {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reviews_id")
	private Long reviewsId;

	@Column(name = "orders_id", nullable = false)
	private Long ordersId;

	@Column(name = "reviewers_id", nullable = false)
	private Long reviewersId;

	@Column(name = "reviewees_id", nullable = false)
	private Long revieweesId;

	@Column(name = "rating", nullable = false)
	private Integer rating;

	@Column(name = "comment", columnDefinition = "TEXT")
	private String comment;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}