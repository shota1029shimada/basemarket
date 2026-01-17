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
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notifications {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;

	@Column(name = "notification_type", nullable = false)
	private String notificationType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "related_item_id")
	private Items relatedItem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "related_order_id")
	private Orders relatedOrder;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String message;

	@Column(name = "is_read", nullable = false)
	private boolean isRead = false;

	@Column(name = "is_sent_line", nullable = false)
	private boolean isSentLine = false;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
	}
}
