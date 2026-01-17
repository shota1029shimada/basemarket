package com.basemarket.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.basemarket.dto.response.ItemResponse;
import com.basemarket.entity.Bookmarks;
import com.basemarket.entity.Items;
import com.basemarket.entity.Users;
import com.basemarket.exception.ResourceNotFoundException;
import com.basemarket.repository.BookmarksRepository;
import com.basemarket.repository.ItemsRepository;
import com.basemarket.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarksService {

	private final BookmarksRepository bookmarksRepository;
	private final ItemsRepository itemsRepository;
	private final SecurityUtil securityUtil;

	/**
	 * 自分のブックマーク一覧
	 * GET /api/bookmarks
	 */
	@Transactional(readOnly = true)
	public List<ItemResponse> getMyBookmarks() {
		Users loginUser = securityUtil.getLoginUser();

		// BookmarksRepository に findByUser が無い場合は追加が必要です。
		// すでに追加済みならこのままでOKです。
		return bookmarksRepository.findByUserOrderByCreatedAtDesc(loginUser)
				.stream()
				.map(b -> new ItemResponse(b.getItem()))
				.toList();
	}

	/**
	 * ブックマーク登録（冪等）
	 * POST /api/bookmarks/{id}
	 */
	public void addBookmark(Long itemId) {
		Users loginUser = securityUtil.getLoginUser();

		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		// 既に登録済みなら何もしない（冪等成功）
		if (bookmarksRepository.existsByUserAndItem(loginUser, item)) {
			return;
		}

		Bookmarks bookmark = Bookmarks.builder()
				.user(loginUser)
				.item(item)
				.build();

		bookmarksRepository.save(bookmark);
	}

	/**
	 * ブックマーク解除（冪等）
	 * DELETE /api/bookmarks/{id}
	 */
	public void removeBookmark(Long itemId) {
		Users loginUser = securityUtil.getLoginUser();

		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		bookmarksRepository.findByUserAndItem(loginUser, item)
				.ifPresent(bookmarksRepository::delete);
	}
}
