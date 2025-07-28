package com.example.ium.recommend.application.dto.request;

import lombok.Builder;
import lombok.Getter;

/**
 * GPT API로 보낼 요청 데이터 DTO
 */
@Getter
@Builder
public class GptRequestDto {
    
    private String model;           // GPT 모델 버전 (예: "gpt-4.1")
    private String input;           // GPT로 보낼 프롬프트 텍스트
    private Integer maxTokens;      // 최대 토큰 수
    private Double temperature;     // 창의성 수준 (0.0 ~ 1.0)
    
    public static GptRequestDto createRecommendationRequest(String prompt) {
        return GptRequestDto.builder()
                .model("gpt-4.1")
                .input(prompt)
                .maxTokens(1000)
                .temperature(0.7)
                .build();
    }
}
