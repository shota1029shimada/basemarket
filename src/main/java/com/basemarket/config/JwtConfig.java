//JWT認証の設定・トークンの有効期限、秘密鍵の設定
package com.basemarket.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * JWT設定値をまとめて保持するための設定クラス。
 *
 * 重要:
 * - 現状の JwtUtil は @Value で完結しているため、このクラスは「整理用」。
 * - 既存クラスの修正なしで追加できる。
 * - 将来 JwtUtil を @ConfigurationProperties 方式に移行したくなったら利用する。
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {

	/**
	 * JWT署名用の秘密鍵（32文字以上推奨）
	 */
	private String secret;

	/**
	 * 有効期限（ミリ秒）
	 */
	private long expiration;
}
