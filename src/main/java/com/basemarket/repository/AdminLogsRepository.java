//admin_logsテーブルへのCRUD操作    
package com.basemarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basemarket.entity.AdminLogs;
import com.basemarket.entity.Users;

@Repository
public interface AdminLogsRepository extends JpaRepository<AdminLogs, Long> {

	// 管理者IDごとの操作履歴
	List<AdminLogs> findByAdminOrderByCreatedAtDesc(Users admin);
}
