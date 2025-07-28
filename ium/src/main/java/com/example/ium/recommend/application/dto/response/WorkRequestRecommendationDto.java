package com.example.ium.recommend.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * EXPERT에게 의뢰를 추천할 때 사용하는 DTO
 */
@Getter
@Builder
public class WorkRequestRecommendationDto {
    
    private Long workRequestId;         // 의뢰 ID
    private String title;               // 의뢰 제목
    private String description;         // 의뢰 설명
    private String category;            // 의뢰 카테고리
    private String clientName;          // 의뢰자 이름
    private Integer budget;             // 예산
    private LocalDateTime deadline;     // 마감일
    private LocalDateTime createdAt;    // 생성일
    private String status;              // 의뢰 상태
    private List<String> requiredSkills; // 필요한 기술/전문분야
    private String location;            // 작업 지역
    private String workType;            // 작업 유형 (온라인/오프라인)
    private String aiRecommendReason;   // AI 추천 이유
    private Double matchingScore;       // 매칭 점수 (0.0 ~ 1.0)
    
    public static WorkRequestRecommendationDto of(Long workRequestId, String title,
                                                String description, String category, String clientName,
                                                Integer budget, LocalDateTime deadline, LocalDateTime createdAt,
                                                String status, List<String> requiredSkills, String location,
                                                String workType, String aiRecommendReason, Double matchingScore) {
        return WorkRequestRecommendationDto.builder()
                .workRequestId(workRequestId)
                .title(title)
                .description(description)
                .category(category)
                .clientName(clientName)
                .budget(budget)
                .deadline(deadline)
                .createdAt(createdAt)
                .status(status)
                .requiredSkills(requiredSkills)
                .location(location)
                .workType(workType)
                .aiRecommendReason(aiRecommendReason)
                .matchingScore(matchingScore)
                .build();
    }
}
