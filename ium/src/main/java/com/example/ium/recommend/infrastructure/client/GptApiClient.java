package com.example.ium.recommend.infrastructure.client;

import com.example.ium.recommend.application.dto.request.GptRequestDto;
import com.example.ium.recommend.application.dto.response.GptResponseDto;

/**
 * GPT API 클라이언트 인터페이스
 * 외부 GPT API와의 통신을 추상화
 */
public interface GptApiClient {
    
    /**
     * GPT API로 추천 요청 전송
     * 
     * @param request GPT 요청 DTO
     * @return GPT 응답 DTO
     * @throws GptApiException API 호출 실패 시
     */
    GptResponseDto sendRecommendationRequest(GptRequestDto request) throws GptApiException;
    
    /**
     * API 연결 상태 확인
     * 
     * @return 연결 가능 여부
     */
    boolean isHealthy();
}
