// notificationsテーブルへのCRUD操作
package com.basemarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basemarket.entity.Notifications;
import com.basemarket.entity.Users;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long> {

	// ユーザーごとの通知一覧（作成日時順）
	List<Notifications> findByUserOrderByCreatedAtDesc(Users user);
}
