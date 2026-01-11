//商品関連のビジネスロジック
//出品、編集、削除、検索処理
package com.basemarket.service;

import java.util.List;// List の import追加

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.basemarket.dto.request.ItemsCreateRequest;
import com.basemarket.entity.Categories;
import com.basemarket.entity.Items;
import com.basemarket.entity.Users;
import com.basemarket.repository.CategoriesRepository;
import com.basemarket.repository.ItemsRepository;
import com.basemarket.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemsService {

	// 商品テーブル操作用
	private final ItemsRepository itemsRepository;
	private final UsersRepository usersRepository;
	private final CategoriesRepository categoriesRepository;

	//商品一覧
	public List<Items> getLatestItems() {
		return itemsRepository.findAllByOrderByCreatedAtDesc();
	}

	//カテゴリ別商品一覧
	public List<Items> getItemsByCategory(Long categoryId) {
		return itemsRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId);
	}

	//商品名検索
	public List<Items> searchItems(String keyword) {
		return itemsRepository.findByTitleContainingOrderByCreatedAtDesc(keyword);
	}

	/**
	 * 商品詳細取得（＋閲覧数を増やす
	 * @param itemId 商品ID
	 * @return 商品エンティティ
	 */
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

	//商品出品操作
	public Items createItem(ItemsCreateRequest request) {

		Users seller = usersRepository.findById(request.getSellerId())
				.orElseThrow(() -> new RuntimeException("出品者が存在しません"));

		Categories category = categoriesRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new RuntimeException("カテゴリが存在しません"));

		Items item = Items.builder()
				.seller(seller)
				.category(category)
				.title(request.getTitle())
				.description(request.getDescription())
				.price(request.getPrice())
				.condition(request.getCondition())
				.build();

		return itemsRepository.save(item);
	}
}
