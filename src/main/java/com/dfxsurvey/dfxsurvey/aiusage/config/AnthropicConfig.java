package com.dfxsurvey.dfxsurvey.aiusage.config;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AiUsageProperties.class)
public class AnthropicConfig {

    // API anahtarı koda yazılmaz; ANTHROPIC_API_KEY ortam değişkeninden okunur.
    @Bean(destroyMethod = "close")
    public AnthropicClient anthropicClient() {
        return AnthropicOkHttpClient.fromEnv();
    }
}
