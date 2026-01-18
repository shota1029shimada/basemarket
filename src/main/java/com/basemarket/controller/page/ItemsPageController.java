package com.basemarket.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.basemarket.dto.response.ItemResponse;
import com.basemarket.service.ItemsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ItemsPageController {

	private final ItemsService itemsService;

	// 商品一覧
	@GetMapping("/items")
	public String index(Model model) {
		model.addAttribute("items", itemsService.getLatestItems());
		return "items/index";
	}

	// 出品画面
	@GetMapping("/items/new")
	public String newItem() {
		return "items/new";
	}

	// 編集画面
	@GetMapping("/items/{id}/edit")
	public String edit(@PathVariable Long id, Model model) {
		model.addAttribute("item", itemsService.getItemById(id));
		return "items/edit";
	}

	// 削除確認画面
	@GetMapping("/items/{id}/delete")
	public String deleteConfirm(@PathVariable Long id, Model model) {
		ItemResponse item = itemsService.getItemById(id);
		model.addAttribute("item", item);
		return "items/delete";
	}

	// 商品詳細
	@GetMapping("/items/{id:\\d+}") //数値制約
	public String detail(@PathVariable Long id, Model model) {
		model.addAttribute("item", itemsService.getItemById(id));
		return "items/detail";
	}

}