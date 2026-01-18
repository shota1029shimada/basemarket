//likesテーブルへのCRUD操作 
package com.basemarket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basemarket.entity.Items;
import com.basemarket.entity.Likes;
import com.basemarket.entity.Users;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

	// ユーザーが特定の商品にいいね済みか確認
	Optional<Likes> findByUserAndItem(Users user, Items item);

	// ユーザーと商品でユニーク制約に合わせる
	boolean existsByUserAndItem(Users user, Items item);
}