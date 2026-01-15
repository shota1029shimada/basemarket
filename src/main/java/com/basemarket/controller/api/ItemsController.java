//商品API・商品一覧・商品詳細・商品出品・商品編集・商品削除
package com.basemarket.controller.api;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.basemarket.dto.request.ItemsCreateRequest;
import com.basemarket.dto.request.ItemsUpdateRequest;
import com.basemarket.dto.response.ItemResponse;
import com.basemarket.entity.Items;
import com.basemarket.service.ItemsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/items") // ← API専用に変更
@RequiredArgsConstructor
public class ItemsController {

	private final ItemsService itemsService;

	// 商品出品（ログイン必須）
	//POST /api/items
	@PostMapping
	public ResponseEntity<ItemResponse> createItem(
			@RequestBody @Valid ItemsCreateRequest request,
			Authentication authentication) {

		ItemResponse response = itemsService.createItem(request, authentication);

		return ResponseEntity.ok(response);
	}

	// 商品一覧（新着順）
	//GET /api/items
	@GetMapping
	public ResponseEntity<List<Items>> getItems() {
		return ResponseEntity.ok(itemsService.getLatestItems());
	}

	// 商品詳細（閲覧数 +1）
	// GET /api/items/{id}
	@GetMapping("/{id}")
	public ResponseEntity<Items> getItem(@PathVariable Long id) {
		return ResponseEntity.ok(itemsService.getItemDetail(id));
	}

	// カテゴリ別商品一覧
	// GET /api/items/category/{categoryId}
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<Items>> getItemsByCategory(
			@PathVariable Long categoryId) {

		return ResponseEntity.ok(
				itemsService.getItemsByCategory(categoryId));
	}

	// 商品検索
	// GET /api/items/search?keyword=xxx
	@GetMapping("/search")
	public ResponseEntity<List<Items>> searchItems(
			@RequestParam String keyword) {

		return ResponseEntity.ok(
				itemsService.searchItems(keyword));
	}

	// 商品編集（出品者本人のみ）
	// PUT /api/items/{id}
	@PutMapping("/{id}")
	public ResponseEntity<Items> updateItem(
			@PathVariable Long id,
			@RequestBody @Valid ItemsUpdateRequest request,
			Authentication authentication) {

		return ResponseEntity.ok(
				itemsService.updateItem(id, request, authentication));
	}

	/**
	 * 商品削除（出品者本人のみ）
	 * DELETE /api/items/{id}
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteItem(
			@PathVariable Long id,
			Authentication authentication) {

		itemsService.deleteItem(id, authentication);
		return ResponseEntity.noContent().build();
	}
}
