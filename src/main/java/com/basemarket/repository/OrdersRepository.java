//ordersテーブルへのCRUD操作
//購入者別、出品者別の検索等
package com.basemarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basemarket.entity.Orders;
import com.basemarket.entity.Users;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

	// 購入者ごとの注文一覧
	List<Orders> findByBuyerOrderByCreatedAtDesc(Users buyer);

	// 出品者ごとの注文一覧
	List<Orders> findBySellerOrderByCreatedAtDesc(Users seller);
}
