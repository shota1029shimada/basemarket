//出品、編集、削除、検索処理
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
@RequiredArgsConstructor
@Transactional
public class ItemsService {

	private final ItemsRepository itemsRepository;
	private final CategoriesRepository categoriesRepository;
	private final SecurityUtil securityUtil;

	// 商品一覧（新着順）
	public List<ItemResponse> getLatestItems() {
		return itemsRepository.findAllByOrderByCreatedAtDesc()
				.stream()
				.map(ItemResponse::new)
				.toList();
	}

	// カテゴリ別商品一覧
	public List<ItemResponse> getItemsByCategory(Long categoryId) {
		Categories category = categoriesRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("カテゴリが存在しません"));

		return itemsRepository.findByCategoryOrderByCreatedAtDesc(category)
				.stream()
				.map(ItemResponse::new)
				.toList();
	}

	// 商品検索
	public List<ItemResponse> searchItems(String keyword) {
		return itemsRepository.findByTitleContainingOrderByCreatedAtDesc(keyword)
				.stream()
				.map(ItemResponse::new)
				.toList();
	}

	// 商品詳細（閲覧数 +1）
	public ItemResponse getItemById(Long itemId) {

		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		item.setViewsCount(item.getViewsCount() + 1);

		return new ItemResponse(item);
	}

	// 商品出品
	public ItemResponse createItem(ItemsCreateRequest request) {

		Users loginUser = securityUtil.getLoginUser();

		Categories category = categoriesRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("カテゴリが存在しません"));

		Items item = new Items();
		item.setSeller(loginUser);
		item.setTitle(request.getTitle());
		item.setDescription(request.getDescription());
		item.setPrice(request.getPrice());
		item.setCondition(request.getCondition());
		item.setCategory(category);
		item.setStatus("AVAILABLE");

		itemsRepository.save(item);

		return new ItemResponse(item);
	}

	// 商品編集
	public ItemResponse updateItem(Long itemId, ItemsUpdateRequest request) {

		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		Users loginUser = securityUtil.getLoginUser();

		if (!item.getSeller().getId().equals(loginUser.getId())) {
			throw new UnauthorizedException("出品者本人のみ編集可能です");
		}

		Categories category = categoriesRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("カテゴリが存在しません"));

		item.setTitle(request.getTitle());
		item.setDescription(request.getDescription());
		item.setPrice(request.getPrice());
		item.setCondition(request.getCondition());
		item.setCategory(category);

		return new ItemResponse(item);
	}

	// 商品削除
	public void deleteItem(Long itemId) {

		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		Users loginUser = securityUtil.getLoginUser();

		if (!item.getSeller().getId().equals(loginUser.getId())) {
			throw new UnauthorizedException("この商品を削除する権限がありません");
		}

		itemsRepository.delete(item);
	}
}
