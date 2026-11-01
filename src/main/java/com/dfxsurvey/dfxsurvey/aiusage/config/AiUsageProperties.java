package com.dfxsurvey.dfxsurvey.aiusage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.properties içindeki "ai-usage.*" ayarları.
 *
 * @param model           kullanılacak Claude modeli
 * @param maxInputTokens  değerlendirilecek prompt için izin verilen en fazla token
 * @param maxOutputTokens Claude'un cevabı (düşünme dahil) için üst sınır
 */
@ConfigurationProperties(prefix = "ai-usage")
public record AiUsageProperties(String model, long maxInputTokens, long maxOutputTokens) {
}
