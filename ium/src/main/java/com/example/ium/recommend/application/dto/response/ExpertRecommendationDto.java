package com.example.ium.recommend.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * USER에게 전문가를 추천할 때 사용하는 DTO
 */
@Getter
@Builder
public class ExpertRecommendationDto {
    
    private Long expertId;              // 전문가 ID
    private String expertName;          // 전문가 이름
    private String introduceMessage;    // 전문가 소개
    private List<String> specializations; // 전문 분야 목록
    private String school;              // 학교
    private String major;               // 전공
    private Integer salary;             // 희망 연봉
    private Boolean negoYn;             // 협상 가능 여부
    private Integer completedRequestCount; // 완료한 의뢰 수
    private String profileImageUrl;     // 프로필 이미지 URL
    private String aiRecommendReason;   // AI 추천 이유
    private Double matchingScore;       // 매칭 점수 (0.0 ~ 1.0)
    
    public static ExpertRecommendationDto of(Long expertId, String expertName, 
                                           String introduceMessage, List<String> specializations,
                                           String school, String major, Integer salary, 
                                           Boolean negoYn, Integer completedRequestCount,
                                           String profileImageUrl, String aiRecommendReason, 
                                           Double matchingScore) {
        return ExpertRecommendationDto.builder()
                .expertId(expertId)
                .expertName(expertName)
                .introduceMessage(introduceMessage)
                .specializations(specializations)
                .school(school)
                .major(major)
                .salary(salary)
                .negoYn(negoYn)
                .completedRequestCount(completedRequestCount)
                .profileImageUrl(profileImageUrl)
                .aiRecommendReason(aiRecommendReason)
                .matchingScore(matchingScore)
                .build();
    }
}
