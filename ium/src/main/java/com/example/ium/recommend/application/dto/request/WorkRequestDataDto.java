package com.example.ium.recommend.application.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * GPT로 보낼 의뢰 정보 DTO
 * USER가 전문가를 찾을 때 사용
 */
@Getter
@Builder
public class WorkRequestDataDto {
    
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
    private String urgency;             // 긴급도
    private String projectScale;        // 프로젝트 규모
    private String communicationStyle;  // 선호하는 소통 방식
    
    public static WorkRequestDataDto of(Long workRequestId, String title, String description,
                                      String category, String clientName, Integer budget,
                                      LocalDateTime deadline, LocalDateTime createdAt, String status,
                                      List<String> requiredSkills, String location, String workType,
                                      String urgency, String projectScale, String communicationStyle) {
        return WorkRequestDataDto.builder()
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
                .urgency(urgency)
                .projectScale(projectScale)
                .communicationStyle(communicationStyle)
                .build();
    }
    
    /**
     * GPT 프롬프트용 텍스트 생성
     */
    public String toPromptText() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("의뢰 정보:\n");
        prompt.append("- 제목: ").append(title).append("\n");
        prompt.append("- 설명: ").append(description).append("\n");
        prompt.append("- 카테고리: ").append(category).append("\n");
        prompt.append("- 의뢰자: ").append(clientName).append("\n");
        prompt.append("- 예산: ").append(budget).append("원\n");
        prompt.append("- 마감일: ").append(deadline).append("\n");
        prompt.append("- 상태: ").append(status).append("\n");
        prompt.append("- 필요 기술: ").append(String.join(", ", requiredSkills)).append("\n");
        prompt.append("- 작업 지역: ").append(location).append("\n");
        prompt.append("- 작업 유형: ").append(workType).append("\n");
        prompt.append("- 긴급도: ").append(urgency).append("\n");
        prompt.append("- 프로젝트 규모: ").append(projectScale).append("\n");
        prompt.append("- 소통 방식: ").append(communicationStyle).append("\n");
        
        return prompt.toString();
    }
    
    /**
     * 남은 기간 계산 (일 단위)
     */
    public long getDaysUntilDeadline() {
        if (deadline == null) return -1;
        return java.time.Duration.between(LocalDateTime.now(), deadline).toDays();
    }
    
    /**
     * 의뢰가 긴급한지 확인
     */
    public boolean isUrgent() {
        return getDaysUntilDeadline() <= 7 || "높음".equals(urgency);
    }
}
