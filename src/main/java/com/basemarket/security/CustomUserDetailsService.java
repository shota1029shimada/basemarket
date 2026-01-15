//# ユーザー認証情報の取得
//# Spring Securityとの連携
package com.basemarket.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.basemarket.entity.Users;
import com.basemarket.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

//Spring Security がログイン時に呼び出すクラス
@Service // Spring に管理される Service クラス
@RequiredArgsConstructor // ← Lombok：finalなフィールドのコンストラクタを自動生成
@Component
public class CustomUserDetailsService implements UserDetailsService {

	// UsersテーブルへアクセスするRepository
	private final UsersRepository usersRepository;

	/**
	 * Spring Security がログイン時に自動で呼び出すメソッド
	 * @param email ログイン画面で入力された「email」
	 * @return UserDetails（Spring Security専用ユーザー情報）
	 */
	@Override
	public UserDetails loadUserByUsername(String email)
			throws UsernameNotFoundException {

		//emailを使ってDBからユーザーを検索
		Users user = usersRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + email));

		//BANされているユーザーはログイン不可
		if (user.isBanned()) {
			throw new UsernameNotFoundException("このユーザーはBANされています");
		}

		//ユーザーの権限を作成
		// Spring Securityでは「ROLE_XXX」という形式が必須
		List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

		//Spring Securityが使うUserDetailsを生成して返却
		return new User(
				user.getEmail(), // ユーザー名（ログインID）
				user.getPasswordHash(), // ハッシュ化されたパスワード
				authorities // 権限（ROLE_USER / ROLE_ADMIN）
		);
	}
}