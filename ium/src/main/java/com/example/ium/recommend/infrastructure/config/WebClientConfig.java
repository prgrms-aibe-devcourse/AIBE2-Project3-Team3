package com.example.ium.recommend.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP 클라이언트 설정
 * GPT API 호출을 위한 HTTP 클라이언트 구성
 */
@Configuration
public class WebClientConfig {
    
    private final GptApiProperties gptApiProperties;
    
    public WebClientConfig(GptApiProperties gptApiProperties) {
        this.gptApiProperties = gptApiProperties;
    }
    
    /**
     * GPT API 전용 RestTemplate 빈 생성 (WebFlux 대신 사용)
     */
    @Bean
    @Qualifier("gptRestTemplate")
    public RestTemplate gptRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(gptApiProperties.getTimeoutSeconds() * 1000);
        factory.setReadTimeout(gptApiProperties.getTimeoutSeconds() * 1000);
        
        return new RestTemplate(factory);
    }
}
