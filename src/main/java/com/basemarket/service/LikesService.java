package com.basemarket.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.basemarket.dto.response.ItemResponse;
import com.basemarket.entity.Items;
import com.basemarket.entity.Likes;
import com.basemarket.entity.Users;
import com.basemarket.exception.ResourceNotFoundException;
import com.basemarket.repository.ItemsRepository;
import com.basemarket.repository.LikesRepository;
import com.basemarket.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LikesService {

	private final LikesRepository likesRepository;
	private final ItemsRepository itemsRepository;
	private final SecurityUtil securityUtil;

	/**
	 * 商品にいいね追加
	 */
	public void addLike(Long itemId) {
		Users loginUser = securityUtil.getLoginUser();

		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		// 既にいいねしている場合は何もしない
		if (likesRepository.existsByItemAndUser(item, loginUser)) {
			return;
		}

		Likes like = Likes.builder()
				.item(item)
				.user(loginUser)
				.build();

		likesRepository.save(like);
	}

	/**
	 * 商品のいいね解除
	 */
	public void removeLike(Long itemId) {
		Users loginUser = securityUtil.getLoginUser();

		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		likesRepository.findByItemAndUser(item, loginUser)
				.ifPresent(likesRepository::delete);
	}

	/**
	 * ログインユーザーがいいねした商品一覧
	 */
	public List<ItemResponse> getLikedItems() {
		Users loginUser = securityUtil.getLoginUser();

		return likesRepository.findAllByUser(loginUser)
				.stream()
				.map(like -> new ItemResponse(like.getItem()))
				.collect(Collectors.toList());
	}
}
