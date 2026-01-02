//商品API・商品一覧・商品詳細・商品出品・商品編集・商品削除
package com.basemarket.controller;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.basemarket.entity.Items;
import com.basemarket.service.ItemsService;

import lombok.RequiredArgsConstructor;

@RestController //HTML を返す Controller（REST ではない）
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemsController {

	// Service 層を注入
	private final ItemsService itemsService;

	@GetMapping
	public String listItems(Model model) {

		// 商品一覧取得
		List<Items> items = itemsService.getLatestItems();

		// View に渡す
		model.addAttribute("items", items);

		// templates/items/list.html を表示
		return "items/list";
	}

	/**
	 * 商品詳細表示（閲覧数 +1）
	 * URL: /items/{id}
	 */
	@GetMapping("/{id}")
	public String itemDetail(@PathVariable Long id, Model model) {

		// 商品詳細取得（＋閲覧数UP）
		Items item = itemsService.getItemDetail(id);

		model.addAttribute("item", item);

		// templates/items/detail.html
		return "items/detail";
	}

	/**
	 * カテゴリ別商品一覧
	 * URL: /items/category/{categoryId}
	 */
	@GetMapping("/category/{categoryId}")
	public String itemsByCategory(
			@PathVariable Long categoryId,
			Model model) {

		List<Items> items = itemsService.getItemsByCategory(categoryId);

		model.addAttribute("items", items);

		return "items/list";
	}

	/**
	 * 商品検索
	 * URL: /items/search?keyword=xxx
	 */
	@GetMapping("/search")
	public String searchItems(
			@RequestParam String keyword,
			Model model) {

		List<Items> items = itemsService.searchItems(keyword);

		model.addAttribute("items", items);
		model.addAttribute("keyword", keyword);

		return "items/list";
	}
}
