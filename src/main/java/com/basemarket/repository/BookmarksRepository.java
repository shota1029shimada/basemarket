////bookmarksテーブルへのCRUD操作
package com.basemarket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basemarket.entity.Bookmarks;
import com.basemarket.entity.Items;
import com.basemarket.entity.Users;

@Repository
public interface BookmarksRepository extends JpaRepository<Bookmarks, Long> {

	Optional<Bookmarks> findByUserAndItem(Users user, Items item);

	boolean existsByUserAndItem(Users user, Items item);

	////ブックマーク一覧（新しい順）
	List<Bookmarks> findByUserOrderByCreatedAtDesc(Users user);
}
