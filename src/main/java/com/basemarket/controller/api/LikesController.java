package com.basemarket.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basemarket.dto.response.ItemResponse;
import com.basemarket.service.LikesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class LikesController {

	private final LikesService likesService;

	/**
	 * いいね追加
	 * POST /api/items/{id}/like
	 */
	@PostMapping("/{id}/like")
	public ResponseEntity<Void> likeItem(@PathVariable Long id) {
		likesService.addLike(id);
		return ResponseEntity.ok().build();
	}

	/**
	 * いいね解除
	 * DELETE /api/items/{id}/like
	 */
	@DeleteMapping("/{id}/like")
	public ResponseEntity<Void> unlikeItem(@PathVariable Long id) {
		likesService.removeLike(id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * ユーザーがいいねした商品一覧取得（任意で実装）
	 * GET /api/items/liked
	 */
	@GetMapping("/liked")
	public ResponseEntity<List<ItemResponse>> getLikedItems() {
		return ResponseEntity.ok(likesService.getLikedItems());
	}
}
