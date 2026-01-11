//usersテーブルへのCRUD操作
//findById(), save(), delete()等のメソッド
package com.basemarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basemarket.entity.Items;

@Repository
public interface ItemsRepository extends JpaRepository<Items, Long> {

	/**
	 * 商品一覧（新着順）
	 */
	List<Items> findAllByOrderByCreatedAtDesc();

	/**
	 * カテゴリIDで商品検索
	 */
	List<Items> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);

	/**
	 * タイトル部分一致検索
	 */
	List<Items> findByTitleContainingOrderByCreatedAtDesc(String keyword);
}
