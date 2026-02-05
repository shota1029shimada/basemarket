//商品に関する画面遷移とフォーム処理を担当
package com.basemarket.controller.page;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.basemarket.dto.request.ItemsCreateRequest;
import com.basemarket.dto.request.ItemsUpdateRequest;
import com.basemarket.dto.response.ItemResponse;
import com.basemarket.repository.CategoriesRepository;
import com.basemarket.service.ItemsService;

import lombok.RequiredArgsConstructor;

@Controller // HTML（Thymeleafテンプレート）を返す画面用コントローラ
// ・return String = テンプレート名
@RequiredArgsConstructor
// → DI（依存性注入）を簡潔に書ける
public class ItemsPageController {

	// final フィールド（ItemsService）を引数に持つコンストラクタを自動生成
	private final ItemsService itemsService;//商品に関する業務ロジックを担当するService
	private final CategoriesRepository categoriesRepository; //

	// 商品一覧画面の表示
	@GetMapping("/items") // GET /items
	// ・未ログインでも閲覧可能（SecurityConfigでpermitAll）
	public String index(Model model) {// Model
		// Controller → View(HTML) にデータを渡すための入れ物
		// items という名前でList<ItemResponse>（想定）をテンプレートに渡す
		model.addAttribute("items", itemsService.getLatestItems());

		// templates/items/index.html を返す
		return "items/index";
	}

	// 出品画面の表示
	@GetMapping("/items/new") // GET /items/new
	// ・ログイン必須（SecurityConfigでauthenticated）
	public String newItem(Model model) {

		// フォーム用の空オブジェクトを渡す
		// Thymeleafの th:object と紐づく
		model.addAttribute("itemsCreateRequest", new ItemsCreateRequest());
		model.addAttribute("categories", categoriesRepository.findAll()); // 追

		// templates/items/new.html
		return "items/new";
	}

	// 出品処理
	@PostMapping("/items") //POST /items
	public String create(
			@Valid ItemsCreateRequest request,
			BindingResult bindingResult,
			Model model) {

		// バリデーションエラーがある場合
		if (bindingResult.hasErrors()) {

			// 入力値を戻す（将来 th:object に戻すため）
			model.addAttribute("itemsCreateRequest", request);

			// カテゴリ一覧（エラー時に再表示用）
			model.addAttribute("categories", categoriesRepository.findAll());

			// ★B案：th:object を使わないため、エラーメッセージを List で渡す
			List<String> errors = bindingResult.getAllErrors()
					.stream()
					.map(error -> error.getDefaultMessage())
					.toList();

			model.addAttribute("errors", errors);

			// 出品画面を再表示
			return "items/new";
		}

		// バリデーションOK → 出品処理
		itemsService.createItem(request);

		// 成功時は一覧画面へ（PRGパターン）
		return "redirect:/items";
	}

	// 削除確認画面
	@GetMapping("/items/{id}/delete")
	// ・実際の削除は行わない
	// ・「本当に削除しますか？」という確認画面用
	public String deleteConfirm(@PathVariable Long id, Model model) {

		// 削除対象の商品情報を表示するため取得
		ItemResponse item = itemsService.getItemById(id);

		model.addAttribute("item", item);

		// templates/items/delete.html
		return "items/delete";
	}

	// 商品削除処理（画面からの削除）
	@DeleteMapping("/items/{id}")
	public String delete(@PathVariable Long id) {
		itemsService.deleteItem(id);
		return "redirect:/items";
	}

	// 商品詳細
	@GetMapping("/items/{id:\\d+}")
	// ✅ 正規表現付き PathVariable
	// {id:\\d+} = 数字のみ許可
	// ・/items/new などの文字列URLと競合しないようにするため
	public String detail(@PathVariable Long id, Model model) {

		// 商品詳細を取得
		model.addAttribute("item", itemsService.getItemById(id));
		// templates/items/detail.html
		return "items/detail";
	}

	// 編集画面の表示
	@GetMapping("/items/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		model.addAttribute("item", itemsService.getItemById(id));
		model.addAttribute("categories", categoriesRepository.findAll());
		return "items/edit";
	}

	// 商品更新（フォーム POST + _method=put → PutMapping）
	@PutMapping("/items/{id}")
	public String update(
			@PathVariable Long id,
			@Valid ItemsUpdateRequest request,
			BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasErrors()) {
			// エラー時は既存の商品情報とリクエストを渡す
			model.addAttribute("item", itemsService.getItemById(id));
			model.addAttribute("itemsUpdateRequest", request);
			model.addAttribute("categories", categoriesRepository.findAll());
			model.addAttribute("errors", bindingResult.getAllErrors().stream()
					.map(error -> error.getDefaultMessage()).toList());
			return "items/edit";
		}
		itemsService.updateItem(id, request);
		return "redirect:/items/" + id;
	}
}
