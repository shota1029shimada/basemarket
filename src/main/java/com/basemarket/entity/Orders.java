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
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "orders_id")
	private Long ordersId;

	@Column(name = "items_id", nullable = false)
	private Long itemsId;

	@Column(name = "buyers_id", nullable = false)
	private Long buyersId;

	@Column(name = "sellers_id", nullable = false)
	private Long sellersId;

	@Column(name = "total_amount", nullable = false)
	private Integer totalAmount;

	@Column(name = "stripe_payment_intent_id", length = 255)
	private String stripePaymentIntentId;

	@Column(name = "order_status", nullable = false, length = 20)
	private String orderStatus = "PENDING";

	@Column(name = "payment_completed_at")
	private LocalDateTime paymentCompletedAt;

	@Column(name = "shipped_at")
	private LocalDateTime shippedAt;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}