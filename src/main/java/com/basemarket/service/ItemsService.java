//商品関連のビジネスロジック
//出品、編集、削除、検索処理
package com.basemarket.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.apache.catalina.security.SecurityUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.basemarket.dto.request.ItemsCreateRequest;
import com.basemarket.dto.request.ItemsUpdateRequest;
import com.basemarket.entity.Categories;
import com.basemarket.entity.Items;
import com.basemarket.entity.Users;
import com.basemarket.exception.ResourceNotFoundException;
import com.basemarket.exception.UnauthorizedException;
import com.basemarket.repository.CategoriesRepository;
import com.basemarket.repository.ItemsRepository;
import com.basemarket.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemsService {

	//Repository
	private final ItemsRepository itemsRepository;
	private final UsersRepository usersRepository;
	private final CategoriesRepository categoriesRepository;

	// Utility
	// JWT からログインユーザーを取得する共通部品
	// ※ 次フェーズで実装予定
	private final SecurityUtil securityUtil;

	// 商品一覧（新着順）
	public List<Items> getLatestItems() {
		return itemsRepository.findAllByOrderByCreatedAtDesc();
	}

	// カテゴリ別商品一覧
	public List<Items> getItemsByCategory(Long categoryId) {
		return itemsRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId);
	}

	//商品検索（タイトル部分一致）
	public List<Items> searchItems(String keyword) {
		return itemsRepository.findByTitleContainingOrderByCreatedAtDesc(keyword);
	}

	// 商品詳細取得（閲覧数 +1）
	public Items getItemDetail(Long itemId) {

		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		// 閲覧数をインクリメント
		if (item.getViewsCount() == null) {
			item.setViewsCount(0);
		}
		item.setViewsCount(item.getViewsCount() + 1);

		// @Transactional により save() 不要
		return item;
	}

	//商品出品
	public Items createItem(ItemsCreateRequest request) {

		// ログインユーザー取得（JWT）
		Users loginUser = securityUtil.getLoginUser();//JWT仕様変更が来ても Service 側だけ修正
		Items item = new Items();
		item.setSeller(loginUser);
		item.setTitle(request.getTitle());
		item.setDescription(request.getDescription());
		item.setPrice(request.getPrice());
		item.setCondition(request.getCondition());
		item.setCategory(
				categoriesRepository.findById(request.getCategoryId())
						.orElseThrow(() -> new ResourceNotFoundException("カテゴリが存在しません")));

		return itemsRepository.save(item);
	}

	//商品編集（出品者本人のみ）
	public Items updateItem(
			Long itemId,
			ItemsUpdateRequest request,
			Authentication authentication) {

		//　ログインユーザー取得
		Users loginUser = (Users) authentication.getPrincipal();

		//  商品存在チェック
		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new RuntimeException("商品が存在しません"));

		// 出品者本人チェック
		if (!item.getSeller().getId().equals(loginUser.getId())) {
			throw new UnauthorizedException("この商品を編集する権限がありません");
		}

		// カテゴリ存在チェック
		Categories category = categoriesRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("カテゴリが存在しません"));

		//  編集可能な項目のみ更新
		item.setTitle(request.getTitle());
		item.setDescription(request.getDescription());
		item.setPrice(request.getPrice());
		item.setCondition(request.getCondition());
		item.setCategory(category);

		// ⑤ 保存（@Transactional によりUPDATE）
		return item;
	}

	// 商品削除（出品者本人のみ）
	public void deleteItem(Long itemId, Authentication authentication) {

		//  商品存在チェック
		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		// ログインユーザー取得
		String email = authentication.getName();
		Users loginUser = usersRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("ユーザーが存在しません"));

		// 出品者本人チェック
		if (!item.getSeller().getId().equals(loginUser.getId())) {
			throw new UnauthorizedException("この商品を削除する権限がありません");
		}

		// 削除実行
		itemsRepository.delete(item);
	}
}
