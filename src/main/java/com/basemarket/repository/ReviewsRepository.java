//reviewsテーブルへのCRUD操作
package com.basemarket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basemarket.entity.Orders;
import com.basemarket.entity.Reviews;
import com.basemarket.entity.Users;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long> {

	// 注文ごとのレビュー取得（1注文1レビュー）
	Optional<Reviews> findByOrderAndReviewer(Orders order, Users reviewer);

	// 被評価者ごとのレビュー一覧
	List<Reviews> findByRevieweeOrderByCreatedAtDesc(Users reviewee);
}
