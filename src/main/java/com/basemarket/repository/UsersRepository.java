//usersテーブルへのCRUD操作
//findById(), save(), delete()等のメソッド
package com.basemarket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basemarket.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByEmail(String email);

	//  追加：メールアドレス重複チェック用
	boolean existsByEmail(String email);
}
/**
 * Usersテーブル専用のRepository
 * Repositoryとは：
 *  - DBに対する操作（SELECT / INSERT / UPDATE / DELETE）を担当
 *  - SQLを書かなくても Spring Data JPA が自動生成してくれる
 */

/**
 * emailでユーザーを1件取得する
 *
 * Optional：
 *  - データが存在しない可能性を安全に扱うためのラッパー
 *  - null を直接返さないのがポイント
 *
 * Spring Data JPAの命名規則により
 * 「SELECT * FROM users WHERE email = ?」が自動生成される
 */