//item_imagesテーブルへのCRUD操作
package com.basemarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basemarket.entity.ItemImages;
import com.basemarket.entity.Items;

@Repository
public interface ItemImagesRepository extends JpaRepository<ItemImages, Long> {

	// 商品ごとの画像一覧（表示順）
	List<ItemImages> findByItemOrderByDisplayOrderAsc(Items item);
}
