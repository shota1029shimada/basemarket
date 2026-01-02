//JWTユーティリティ・トークン生成、検証、ユーザー情報抽出
//JWTを扱うためのユーティリティクラス
package com.basemarket.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component // Spring管理Bean（どこからでもDI可能）
public class JwtUtil {

	/**
	 * application.properties から秘密鍵を取得
	 * ※ 必ず十分に長いランダム文字列にすること（32文字以上推奨）
	 */
	@Value("${jwt.secret}")
	private String secret;

	/**
	 * トークンの有効期限（ミリ秒）
	 * 例：86400000 = 24時間
	 */
	@Value("${jwt.expiration}")
	private long expiration;

	/**
	 * 署名に使用する秘密鍵オブジェクト
	 * jjwt 0.12.x では SecretKey を明示的に使う
	 */
	private SecretKey secretKey;

	/**
	 * @PostConstruct
	 * Springがこのクラスを生成した直後に1回だけ呼ばれる
	 *
	 * 文字列の秘密鍵 → JWT用のSecretKeyに変換
	 */
	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(
				secret.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * JWTトークンを生成する
	 *
	 * @param username ログインユーザーの識別子（emailなど）
	 * @return JWTトークン文字列
	 */
	public String generateToken(String username) {

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expiration);

		return Jwts.builder()
				.subject(username) // トークンの主体（誰のトークンか）
				.issuedAt(now) // 発行日時
				.expiration(expiryDate) // 有効期限
				.signWith(secretKey) // 署名（0.12.xではアルゴリズム指定不要）
				.compact(); // トークン文字列として完成
	}

	/**
	 * JWTトークンからユーザー名を取得
	 *
	 * @param token JWTトークン
	 * @return username
	 */
	public String getUsernameFromToken(String token) {
		return getClaims(token).getSubject();
	}

	/**
	 * トークンが有効かどうかを検証する
	 *
	 * ・改ざんされていないか
	 * ・期限切れでないか
	 *
	 * @param token JWTトークン
	 * @return true = 有効 / false = 無効
	 */
	public boolean validateToken(String token) {
		try {
			getClaims(token);
			return true;
		} catch (Exception e) {
			// 署名不正・期限切れ・形式不正など全てここに来る
			return false;
		}
	}

	/**
	 * JWTの中身（Claims）を取得
	 *
	 * jjwt 0.12.x の正式なパース方法
	 */
	private Claims getClaims(String token) {
		return Jwts.parser()
				.verifyWith(secretKey) // 署名検証
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
