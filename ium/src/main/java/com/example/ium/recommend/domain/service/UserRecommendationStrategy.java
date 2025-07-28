package com.example.ium.recommend.domain.service;

import com.example.ium.member.domain.model.Role;
import com.example.ium.recommend.application.dto.response.ExpertRecommendationDto;
import com.example.ium.recommend.application.dto.response.RecommendationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 일반 사용자용 전문가 추천 전략
 * USER 역할 회원에게 적절한 전문가를 추천
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserRecommendationStrategy implements RecommendationStrategy {
    
    private final ExpertDataCollectionService expertDataCollectionService;
    private final GptRecommendationService gptRecommendationService;
    
    @Override
    public RecommendationResponseDto recommend(Long memberId, String category, String userMessage) {
        log.info("사용자 {}에 대한 전문가 추천 시작 - 카테고리: {}", memberId, category);
        
        try {
            // 1. 사용자 정보 수집
            String userProfileData = expertDataCollectionService.collectUserProfileData(memberId);
            
            // 2. 해당 카테고리의 전문가들 정보 수집
            String expertProfilesData = expertDataCollectionService.collectExpertProfilesData(category);
            
            // 3. GPT API를 통한 추천 요청
            List<ExpertRecommendationDto> recommendedExperts = gptRecommendationService
                    .getExpertRecommendations(userProfileData, expertProfilesData, userMessage, category);
            
            // 4. AI 응답 메시지 생성
            String aiMessage = gptRecommendationService.generateExpertRecommendationMessage(
                    recommendedExperts, category, userMessage);
            
            return RecommendationResponseDto.createExpertRecommendationResponse(recommendedExperts, aiMessage);
            
        } catch (Exception e) {
            log.error("사용자 추천 중 오류 발생: {}", e.getMessage(), e);
            return RecommendationResponseDto.createErrorResponse("추천 시스템에 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }
    
    @Override
    public boolean supports(String role) {
        return Role.USER.name().equals(role);
    }
}
