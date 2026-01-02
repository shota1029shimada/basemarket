//商品関連のビジネスロジック
//出品、編集、削除、検索処理
package com.basemarket.service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.basemarket.entity.Items;
import com.basemarket.repository.ItemsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemsService {

	// 商品テーブル操作用
	private final ItemsRepository itemsRepository;

	//商品一覧
	public List<Items> getLatestItems() {
		return itemsRepository.findAllByOrderByCreatedAtDesc();
	}

	/**
	 * 商品詳細取得（＋閲覧数を増やす）
	 *
	 * @param itemId 商品ID
	 * @return 商品エンティティ
	 */
	@Transactional
	public Items getItemDetail(Long itemId) {

		// 商品をDBから取得
		Items item = itemsRepository.findById(itemId)//item は 管理状態。メソッド終了時に 自動で UPDATE
				.orElseThrow(() -> new RuntimeException("商品が存在しません"));

		// 閲覧数を +1
		// ※ null対策（念のため）
		if (item.getViewsCount() == null) {
			item.setViewsCount(0);
		}
		item.setViewsCount(item.getViewsCount() + 1);

		// save() は不要
		// @Transactional により、メソッド終了時に自動で UPDATE される

		return item;
	}
}
