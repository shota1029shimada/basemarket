package com.basemarket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	 * いいね追加（冪等）
	 */
	public void addLike(Long itemId) {

		Users loginUser = securityUtil.getLoginUser();

		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		// 既にいいね済みなら何もしない（冪等成功）
		if (likesRepository.existsByUserAndItem(loginUser, item)) {
			return;
		}

		Likes like = Likes.builder()
				.user(loginUser)
				.item(item)
				.build();

		likesRepository.save(like);
	}

	/**
	 * いいね解除（冪等）
	 */
	public void removeLike(Long itemId) {

		Users loginUser = securityUtil.getLoginUser();

		Items item = itemsRepository.findById(itemId)
				.orElseThrow(() -> new ResourceNotFoundException("商品が存在しません"));

		likesRepository.findByUserAndItem(loginUser, item)
				.ifPresent(likesRepository::delete);
	}
}
