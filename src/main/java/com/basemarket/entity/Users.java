//ユーザー情報を扱うためのJavaクラス
//users テーブル自動生成
package com.basemarket.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * users テーブルに対応する Entity クラス
 * 「DBの1行 = このクラスの1インスタンス」
 */
@Entity // ← このクラスがDBのテーブルと紐づくことを示す
@Table(name = "users") // ← テーブル名を明示
@Getter
@Setter // ← Lombok：getter/setterを自動生成
@NoArgsConstructor // ← 引数なしコンストラクタ生成（JPA必須）
@AllArgsConstructor // ← 全引数コンストラクタ生成
@Builder // ← Users.builder() でオブジェクト生成可能
public class Users {

	@Id // ← 主キー
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "users_id")
	// ↑ DB側でIDを自動採番（PostgreSQLのserial相当）
	private Long id;

	@Column(nullable = false) // NOT NULL制約
	private String username;

	@Column(nullable = false, unique = true) //unique=true：メールアドレス重複不可
	private String email;

	@Column(nullable = false) // パスワード（※後で必ずハッシュ化）
	private String passwordHash;

	@Column(nullable = false) // ROLE_USER / ROLE_ADMIN など
	private String role;

	@Column(nullable = false)
	private boolean isBanned = false;

	@Column(name = "created_at") // 登録日時
	private LocalDateTime createdAt;

	@Column(name = "updated_at") // 更新日時
	private LocalDateTime updatedAt;

	/**
	 * @PrePersist：保存前フック
	 * Entityが保存される直前に自動実行される
	 */
	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	/**
	 * Entityが更新される直前に自動実行される
	 */
	@PreUpdate
	public void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}