package com.example.ium.recommend.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * AI 추천 결과를 담는 공통 응답 DTO
 */
@Getter
@Builder
public class RecommendationResponseDto {
    
    private String userRole;            // USER 또는 EXPERT
    private String recommendationType;  // EXPERT_RECOMMENDATION 또는 WORK_REQUEST_RECOMMENDATION
    private List<ExpertRecommendationDto> expertRecommendations;        // 전문가 추천 목록
    private List<WorkRequestRecommendationDto> workRequestRecommendations; // 의뢰 추천 목록
    private String aiMessage;           // AI가 생성한 메시지
    private boolean success;            // 성공 여부
    private String errorMessage;       // 에러 메시지
    
    /**
     * USER용 전문가 추천 응답 생성
     */
    public static RecommendationResponseDto createExpertRecommendationResponse(
            List<ExpertRecommendationDto> experts, String aiMessage) {
        return RecommendationResponseDto.builder()
                .userRole("USER")
                .recommendationType("EXPERT_RECOMMENDATION")
                .expertRecommendations(experts)
                .aiMessage(aiMessage)
                .success(true)
                .build();
    }
    
    /**
     * EXPERT용 의뢰 추천 응답 생성
     */
    public static RecommendationResponseDto createWorkRequestRecommendationResponse(
            List<WorkRequestRecommendationDto> workRequests, String aiMessage) {
        return RecommendationResponseDto.builder()
                .userRole("EXPERT")
                .recommendationType("WORK_REQUEST_RECOMMENDATION")
                .workRequestRecommendations(workRequests)
                .aiMessage(aiMessage)
                .success(true)
                .build();
    }
    
    /**
     * 에러 응답 생성
     */
    public static RecommendationResponseDto createErrorResponse(String errorMessage) {
        return RecommendationResponseDto.builder()
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }
}
