//Cloudinary接続設定・画像アップロードサービスの初期化
package com.basemarket.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

import lombok.Getter;
import lombok.Setter;

/**
 * Cloudinary設定（共存型）
 * - 設定値をまとめて保持
 * - Cloudinary Bean を用意（必要になったらDIして利用）
 */
@Configuration
@ConfigurationProperties(prefix = "cloudinary")
@Getter
@Setter
public class CloudinaryConfig {

	private String cloudName;
	private String apiKey;
	private String apiSecret;

	@Bean
	public Cloudinary cloudinary() {
		Map<String, String> config = new HashMap<>();
		config.put("cloud_name", cloudName);
		config.put("api_key", apiKey);
		config.put("api_secret", apiSecret);
		return new Cloudinary(config);
	}
}
