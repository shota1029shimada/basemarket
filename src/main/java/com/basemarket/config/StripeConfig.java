//Stripe決済の設定・決済APIの初期化
package com.basemarket.config;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
//import com.stripe.Stripe;

import lombok.Getter;
import lombok.Setter;

/**
 * Stripe設定（共存型）
 * - stripe.api-key を読み込み、Stripe.apiKey に反映
 * - webhookSecret 等もまとめて保持
 */
@Configuration
@ConfigurationProperties(prefix = "stripe")
@Getter
@Setter
public class StripeConfig {

	private String apiKey;
	private String webhookSecret;

	@PostConstruct
	public void init() {
		// apiKey が設定されている場合のみ反映（共存型で安全）
		if (apiKey != null && !apiKey.isBlank()) {
			//Stripe.apiKey = apiKey;
		}
	}
}
