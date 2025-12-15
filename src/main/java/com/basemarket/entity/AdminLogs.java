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
@Table(name = "admin_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLogs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "admin_logs_id")
	private Long adminLogsId;

	@Column(name = "admins_id", nullable = false)
	private Long adminsId;

	@Column(name = "action_type", nullable = false, length = 50)
	private String actionType;

	@Column(name = "target_users_id")
	private Long targetUsersId;

	@Column(name = "target_items_id")
	private Long targetItemsId;

	@Column(name = "reason", columnDefinition = "TEXT")
	private String reason;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}