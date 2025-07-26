package com.example.ium.recommend.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * GPT API 설정 프로퍼티
 * application.yml에서 gpt 관련 설정을 바인딩
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "gpt.api")
public class GptApiProperties {
    
    private String baseUrl = "https://api.openai.com/v1";
    private String apiKey;
    private String model = "gpt-4";
    private Integer maxTokens = 1000;
    private Double temperature = 0.7;
    private Integer timeoutSeconds = 30;
    private Integer retryCount = 3;
    
    /**
     * API 키 유효성 검증
     */
    public boolean isValidApiKey() {
        return apiKey != null && !apiKey.trim().isEmpty() && apiKey.startsWith("sk-");
    }
}
