package com.basemarket.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basemarket.dto.response.ActionResponse;
import com.basemarket.dto.response.ItemResponse;
import com.basemarket.service.BookmarksService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarksController {

	private final BookmarksService bookmarksService;

	/**
	 * ブックマーク一覧
	 * GET /api/bookmarks
	 */
	@GetMapping
	public ResponseEntity<List<ItemResponse>> getBookmarks() {
		return ResponseEntity.ok(bookmarksService.getMyBookmarks());
	}

	/**
	 * ブックマーク登録
	 * POST /api/bookmarks/{id}
	 */
	@PostMapping("/{id}")
	public ResponseEntity<ActionResponse> addBookmark(@PathVariable Long id) {
		bookmarksService.addBookmark(id);
		return ResponseEntity.ok(new ActionResponse("bookmarked"));
	}

	/**
	 * ブックマーク解除
	 * DELETE /api/bookmarks/{id}
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<ActionResponse> removeBookmark(@PathVariable Long id) {
		bookmarksService.removeBookmark(id);
		return ResponseEntity.ok(new ActionResponse("unbookmarked"));
	}
}
