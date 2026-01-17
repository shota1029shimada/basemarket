package com.basemarket.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.basemarket.service.BookmarksService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BookmarksPageController {

	private final BookmarksService bookmarksService;

	/**
	 * ブックマーク一覧画面
	 * GET /bookmarks
	 */
	@GetMapping("/bookmarks")
	public String index(Model model) {
		model.addAttribute("items", bookmarksService.getMyBookmarks());
		return "bookmarks/index";
	}
}
