package com.example.ium.recommend.domain.service;

import com.example.ium.recommend.application.dto.response.RecommendationResponseDto;

/**
 * 추천 전략 인터페이스
 * 사용자 역할(USER/EXPERT)에 따른 추천 전략을 추상화
 */
public interface RecommendationStrategy {
    
    /**
     * 추천 결과 생성
     * 
     * @param memberId 요청자 회원 ID
     * @param category 추천 카테고리
     * @param userMessage 사용자 메시지
     * @return 추천 결과 DTO
     */
    RecommendationResponseDto recommend(Long memberId, String category, String userMessage);
    
    /**
     * 지원하는 역할인지 확인
     * 
     * @param role 회원 역할 (USER/EXPERT)
     * @return 지원 여부
     */
    boolean supports(String role);
}
