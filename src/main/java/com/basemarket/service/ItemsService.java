package com.basemarket.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.basemarket.dto.request.ItemsCreateRequest;
import com.basemarket.dto.request.ItemsUpdateRequest;
import com.basemarket.dto.response.ItemResponse;
import com.basemarket.entity.Categories;
import com.basemarket.entity.Items;
import com.basemarket.entity.Users;
import com.basemarket.exception.ResourceNotFoundException;
import com.basemarket.exception.UnauthorizedException;
import com.basemarket.repository.CategoriesRepository;
import com.basemarket.repository.ItemsRepository;
import com.basemarket.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
// Service層（業務ロジック担当）
@RequiredArgsConstructor
// finalフィールドのコンストラクタを自動生成（DI用）
@Transactional
// このクラス内のDB操作を1トランザクションで管理
public class ItemsService {

	private final ItemsRepository itemsRepository;
	private final CategoriesRepository categoriesRepository;
	private final SecurityUtil securityUtil;

	// 商品一覧（新着順）
	public List<ItemResponse> getLatestItems() {
		// DB → Entity → Response DTO に変換
		return itemsRepository.findAllByOrderByCreatedAtDesc()
				.stream()
				.map(ItemResponse::new)
				.toList();
	}

	// カテゴリ別商品一覧
	public List<ItemResponse> getItemsByCategory(Long categoryId) {
		// カテゴリ存在チェック
		Categories category = categoriesRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("カテゴリが存在しません"));

		// カテゴリに紐づく商品を取得
		return itemsRepository.findByCategoryOrderByCreatedAtDesc(category)
				.stream()
				.map(ItemResponse::new)
				.toList();
	}

	// 商品検索
	public List<ItemResponse> searchItems(String keyword) {
		// タイトル部分一致検索
		return itemsRepository.findByTitleContainingOrderByCreatedAtDesc(keyword)
				.stream()
				.map(ItemResponse::new)
				.toList();
	}

	// 商品詳細（閲覧数 +1）
	public ItemResponse getItemById(Long itemId) {

		// 商品取得（存在しなければ例外）
		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		// 閲覧数インクリメント
		// ※ @Transactional により save() しなくても更新される
		item.setViewsCount(item.getViewsCount() + 1);

		return new ItemResponse(item);
	}

	// 商品出品
	public ItemResponse createItem(ItemsCreateRequest request) {

		// ログインユーザー取得（SecurityContext から）
		Users loginUser = securityUtil.getLoginUser();

		// カテゴリ存在チェック
		Categories category = categoriesRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("カテゴリが存在しません"));

		// Entity作成
		Items item = new Items();
		item.setSeller(loginUser);
		item.setTitle(request.getTitle());
		item.setDescription(request.getDescription());
		item.setPrice(request.getPrice());
		item.setCondition(request.getCondition());
		item.setCategory(category);
		item.setStatus("AVAILABLE");

		// DB保存
		itemsRepository.save(item);

		return new ItemResponse(item);
	}

	// 商品編集
	public ItemResponse updateItem(Long itemId, ItemsUpdateRequest request) {

		// 商品取得
		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		// ログインユーザー取得
		Users loginUser = securityUtil.getLoginUser();

		// 出品者本人チェック
		if (!item.getSeller().getId().equals(loginUser.getId())) {
			throw new UnauthorizedException("出品者本人のみ編集可能です");
		}

		// カテゴリ存在チェック
		Categories category = categoriesRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("カテゴリが存在しません"));

		// 更新処理
		item.setTitle(request.getTitle());
		item.setDescription(request.getDescription());
		item.setPrice(request.getPrice());
		item.setCondition(request.getCondition());
		item.setCategory(category);

		return new ItemResponse(item);
	}

	// 商品削除
	public void deleteItem(Long itemId) {

		// 商品取得
		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		// ログインユーザー取得
		Users loginUser = securityUtil.getLoginUser();
		System.out.println("LOGIN USER => id=" + loginUser.getId() + ", email=" + loginUser.getEmail());

		// 権限チェック（出品者のみ）
		if (!item.getSeller().getId().equals(loginUser.getId())) {
			throw new UnauthorizedException("この商品を削除する権限がありません");
		}

		// 削除
		itemsRepository.delete(item);
	}
}
