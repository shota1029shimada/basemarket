package com.basemarket.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basemarket.service.LikesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class LikesController {

	private final LikesService likesService;

	@PostMapping("/{id}/like")
	public ResponseEntity<Void> likeItem(@PathVariable Long id) {
		likesService.addLike(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}/like")
	public ResponseEntity<Void> unlikeItem(@PathVariable Long id) {
		likesService.removeLike(id);
		return ResponseEntity.noContent().build();
	}
}
