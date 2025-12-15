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
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessages {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chat_messages_id")
	private Long chatMessagesId;

	@Column(name = "items_id", nullable = false)
	private Long itemsId;

	@Column(name = "senders_id", nullable = false)
	private Long sendersId;

	@Column(name = "receivers_id", nullable = false)
	private Long receiversId;

	@Column(name = "message_text", nullable = false, columnDefinition = "TEXT")
	private String messageText;

	@Column(name = "is_read", nullable = false)
	private Boolean isRead = false;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}