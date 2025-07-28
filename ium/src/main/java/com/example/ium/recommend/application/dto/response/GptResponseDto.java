package com.example.ium.recommend.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * GPT API로부터 받을 응답 데이터 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GptResponseDto {
    
    private String outputText;      // GPT 응답 텍스트
    private String finishReason;    // 완료 이유 (stop, length, etc.)
    private Integer totalTokens;    // 사용된 총 토큰 수
    
    public boolean isSuccessful() {
        return outputText != null && !outputText.trim().isEmpty();
    }
}
