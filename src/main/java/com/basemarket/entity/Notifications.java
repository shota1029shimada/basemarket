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
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notifications {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notifications_id")
	private Long notificationsId;

	@Column(name = "users_id", nullable = false)
	private Long usersId;

	@Column(name = "notification_type", nullable = false, length = 50)
	private String notificationType;

	@Column(name = "related_items_id")
	private Long relatedItemsId;

	@Column(name = "related_orders_id")
	private Long relatedOrdersId;

	@Column(name = "message", nullable = false, columnDefinition = "TEXT")
	private String message;

	@Column(name = "is_read", nullable = false)
	private Boolean isRead = false;

	@Column(name = "is_sent_line", nullable = false)
	private Boolean isSentLine = false;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}