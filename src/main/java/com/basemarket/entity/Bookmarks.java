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
@Table(name = "bookmarks", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "users_id", "items_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bookmarks {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bookmarks_id")
	private Long bookmarksId;

	@Column(name = "users_id", nullable = false)
	private Long usersId;

	@Column(name = "items_id", nullable = false)
	private Long itemsId;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}