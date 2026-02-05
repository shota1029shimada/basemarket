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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		
		// デバッグログ：ログインユーザーの確認
		if (loginUser == null) {
			log.error("[BookmarksService] loginUser is null!");
			throw new ResourceNotFoundException("ログインユーザーが取得できません");
		}
		log.debug("[BookmarksService] addBookmark - loginUser.id={}, loginUser.email={}", 
				loginUser.getId(), loginUser.getEmail());

		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));
		
		// デバッグログ：商品の確認
		if (item == null) {
			log.error("[BookmarksService] item is null! itemId={}", itemId);
			throw new ResourceNotFoundException("商品が存在しません");
		}
		log.debug("[BookmarksService] addBookmark - item.id={}, item.title={}", 
				item.getId(), item.getTitle());

		// 既に登録済みなら何もしない（冪等成功）
		if (bookmarksRepository.existsByUserAndItem(loginUser, item)) {
			log.debug("[BookmarksService] Bookmark already exists for user.id={}, item.id={}", 
					loginUser.getId(), item.getId());
			return;
		}

		// Builderではなく直接インスタンス化して確実に設定
		Bookmarks bookmark = new Bookmarks();
		bookmark.setUser(loginUser);
		bookmark.setItem(item);
		// createdAtは@PrePersistで自動設定される

		log.debug("[BookmarksService] Saving bookmark - user.id={}, item.id={}", 
				loginUser.getId(), item.getId());
		bookmarksRepository.save(bookmark);
		log.debug("[BookmarksService] Bookmark saved successfully");
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
