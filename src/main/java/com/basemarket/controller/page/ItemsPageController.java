//商品に関する画面遷移とフォーム処理を担当
package com.basemarket.controller.page;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.basemarket.dto.request.ItemsCreateRequest;
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
	// ・出品フォームの送信先
	// ・バリデーション → エラーなら再表示、OKなら登録
	public String create(
			@Valid ItemsCreateRequest request,
			BindingResult bindingResult,
			Model model) {

		// ✅ @Valid
		// ItemsCreateRequest に付与された
		// @NotBlank / @Size / @NotNull などの制約を検証する

		// ✅ BindingResult
		// バリデーション結果が格納される
		// ※ @Valid の直後に書く必要がある（Springのルール）
		if (bindingResult.hasErrors()) {

			// ❌ 入力エラーがある場合
			// ・入力値をそのまま画面に戻す
			// ・エラーメッセージはThymeleaf側で表示
			model.addAttribute("itemsCreateRequest", request);

			// 再度 出品画面を表示
			return "items/new";
		}

		// ✅ バリデーションOKの場合
		// 実際の登録処理は Service に委譲
		itemsService.createItem(request);

		// PRGパターン
		// ・POST → redirect → GET
		// ・二重送信防止
		return "redirect:/items";
	}

	// 編集画面
	@GetMapping("/items/{id}/edit")
	// ✅ PathVariable
	// URLの {id} 部分を引数として受け取る
	// 例： /items/10/edit → id = 10
	public String edit(@PathVariable Long id, Model model) {

		// 編集対象の商品を取得
		model.addAttribute("item", itemsService.getItemById(id));

		// templates/items/edit.html
		return "items/edit";
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
}
