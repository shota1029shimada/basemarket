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
import org.springframework.web.bind.annotation.RequestParam;

import com.basemarket.dto.request.ItemsCreateRequest;
import com.basemarket.dto.request.ItemsUpdateRequest;
import com.basemarket.dto.response.ItemResponse;
import com.basemarket.entity.Items;
import com.basemarket.entity.Users;
import com.basemarket.exception.UnauthorizedException;
import com.basemarket.repository.CategoriesRepository;
import com.basemarket.repository.ItemsRepository;
import com.basemarket.service.ItemsService;
import com.basemarket.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Controller // HTML（Thymeleafテンプレート）を返す画面用コントローラ
// ・return String = テンプレート名
@RequiredArgsConstructor
// → DI（依存性注入）を簡潔に書ける
public class ItemsPageController {

	// final フィールド（ItemsService）を引数に持つコンストラクタを自動生成
	private final ItemsService itemsService;//商品に関する業務ロジックを担当するService
	private final CategoriesRepository categoriesRepository; //
	private final ItemsRepository itemsRepository;
	private final SecurityUtil securityUtil;

	// 商品一覧画面の表示
	@GetMapping("/items") // GET /items
	// ・未ログインでも閲覧可能（SecurityConfigでpermitAll）
	public String index(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Long categoryId,
			Model model) {
		
		// カテゴリー一覧を取得（検索フォーム用）
		model.addAttribute("categories", categoriesRepository.findAll());
		
		// 検索キーワードとカテゴリーIDの両方が指定されている場合
		if (keyword != null && !keyword.trim().isEmpty() && categoryId != null) {
			// 検索 + カテゴリー絞り込み
			model.addAttribute("items", itemsService.searchItemsByCategory(keyword.trim(), categoryId));
			model.addAttribute("keyword", keyword.trim());
			model.addAttribute("selectedCategoryId", categoryId);
		} else if (keyword != null && !keyword.trim().isEmpty()) {
			// 検索機能のみ
			model.addAttribute("items", itemsService.searchItems(keyword.trim()));
			model.addAttribute("keyword", keyword.trim());
		} else if (categoryId != null) {
			// カテゴリー別表示のみ
			model.addAttribute("items", itemsService.getItemsByCategory(categoryId));
			model.addAttribute("selectedCategoryId", categoryId);
		} else {
			// 通常の一覧表示（新着順）
			model.addAttribute("items", itemsService.getLatestItems());
		}

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

		// 商品取得
		Items item = itemsRepository.findById(id)
				.orElseThrow(() -> new com.basemarket.exception.ResourceNotFoundException("商品が存在しません"));

		// ログインユーザー取得
		Users loginUser = securityUtil.getLoginUser();

		// 権限チェック：出品者本人または管理者のみアクセス可能
		boolean isOwner = item.getSeller().getId().equals(loginUser.getId());
		boolean isAdmin = securityUtil.isAdmin();
		
		if (!isOwner && !isAdmin) {
			throw new UnauthorizedException("出品者本人または管理者のみ削除可能です");
		}

		// 削除対象の商品情報を表示するため取得
		ItemResponse itemResponse = itemsService.getItemById(id);
		model.addAttribute("item", itemResponse);

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
		ItemResponse item = itemsService.getItemById(id);
		model.addAttribute("item", item);

		// 編集・削除ボタンの表示判定（出品者本人または管理者のみ）
		boolean canEditOrDelete = false;
		try {
			Items itemEntity = itemsRepository.findById(id)
					.orElseThrow(() -> new com.basemarket.exception.ResourceNotFoundException("商品が存在しません"));
			Users loginUser = securityUtil.getLoginUser();
			boolean isOwner = itemEntity.getSeller().getId().equals(loginUser.getId());
			boolean isAdmin = securityUtil.isAdmin();
			canEditOrDelete = isOwner || isAdmin;
		} catch (Exception e) {
			// 未ログインまたはエラーの場合は編集・削除不可
			canEditOrDelete = false;
		}
		model.addAttribute("canEditOrDelete", canEditOrDelete);

		// templates/items/detail.html
		return "items/detail";
	}

	// 編集画面の表示
	@GetMapping("/items/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		// 商品取得
		Items item = itemsRepository.findById(id)
				.orElseThrow(() -> new com.basemarket.exception.ResourceNotFoundException("商品が存在しません"));

		// ログインユーザー取得
		Users loginUser = securityUtil.getLoginUser();

		// 権限チェック：出品者本人または管理者のみアクセス可能
		boolean isOwner = item.getSeller().getId().equals(loginUser.getId());
		boolean isAdmin = securityUtil.isAdmin();
		
		if (!isOwner && !isAdmin) {
			throw new UnauthorizedException("出品者本人または管理者のみ編集可能です");
		}

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
