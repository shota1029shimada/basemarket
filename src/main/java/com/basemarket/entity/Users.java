//ユーザー情報を扱うためのJavaクラス
package com.basemarket.entity;

import java.math.BigDecimal;
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

@Entity //データベースのレコードに対応するオブジェクト
@Table(name = "users") //テーブルを指定
@Data //Lombokが自動的にgetter/setterを生成
@NoArgsConstructor //
@AllArgsConstructor
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //データベース側（PostgreSQLなど）で自動採番
	@Column(name = "users_id")
	private Long usersId; // ユーザーID

	@Column(name = "username", unique = true, nullable = false, length = 50)
	private String username;//ユーザー名

	@Column(name = "email", unique = true, nullable = false, length = 100)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Column(name = "profile_image_url", length = 500)
	private String profileImageUrl;

	@Column(name = "bio", columnDefinition = "TEXT")
	private String bio;

	@Column(name = "rating", precision = 3, scale = 2)
	private BigDecimal rating = BigDecimal.ZERO;

	@Column(name = "is_banned", nullable = false)
	private Boolean isBanned = false;

	@Column(name = "role", nullable = false, length = 20)
	private String role = "USER";

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}