package com.example.ium.recommend.domain.service;

import com.example.ium.member.domain.model.Role;
import com.example.ium.recommend.application.dto.response.RecommendationResponseDto;
import com.example.ium.recommend.application.dto.response.WorkRequestRecommendationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 전문가용 의뢰 추천 전략
 * EXPERT 역할 회원에게 적절한 작업 의뢰를 추천
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ExpertRecommendationStrategy implements RecommendationStrategy {
    
    private final WorkRequestDataCollectionService workRequestDataCollectionService;
    private final ExpertDataCollectionService expertDataCollectionService;
    private final GptRecommendationService gptRecommendationService;
    
    @Override
    public RecommendationResponseDto recommend(Long memberId, String category, String userMessage) {
        log.info("전문가 {}에 대한 의뢰 추천 시작 - 카테고리: {}", memberId, category);
        
        try {
            // 1. 전문가 프로필 정보 수집
            String expertProfileData = expertDataCollectionService.collectExpertProfileData(memberId);
            
            // 2. 해당 카테고리의 의뢰들 정보 수집
            String workRequestsData = workRequestDataCollectionService.collectWorkRequestsData(category);
            
            // 3. GPT API를 통한 추천 요청
            List<WorkRequestRecommendationDto> recommendedWorkRequests = gptRecommendationService
                    .getWorkRequestRecommendations(expertProfileData, workRequestsData, userMessage, category);
            
            // 4. AI 응답 메시지 생성
            String aiMessage = gptRecommendationService.generateWorkRequestRecommendationMessage(
                    recommendedWorkRequests, category, userMessage);
            
            return RecommendationResponseDto.createWorkRequestRecommendationResponse(recommendedWorkRequests, aiMessage);
            
        } catch (Exception e) {
            log.error("전문가 추천 중 오류 발생: {}", e.getMessage(), e);
            return RecommendationResponseDto.createErrorResponse("추천 시스템에 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }
    
    @Override
    public boolean supports(String role) {
        return Role.EXPERT.name().equals(role);
    }
}
