package com.example.ium.recommend.application.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * GPT로 보낼 사용자 정보 DTO
 * USER가 전문가를 찾을 때 사용
 */
@Getter
@Builder
public class UserProfileDataDto {
    
    private Long userId;                // 사용자 ID
    private String username;            // 사용자명
    private String preferredCategory;   // 선호하는 서비스 카테고리
    private String requestDescription;  // 요청 설명
    private Integer budget;             // 예산
    private String location;            // 지역
    private String urgency;             // 긴급도 (높음/보통/낮음)
    private List<String> requiredSkills; // 필요한 기술/전문분야
    private String projectScale;        // 프로젝트 규모 (소규모/중규모/대규모)
    private String communicationStyle;  // 선호하는 소통 방식
    
    public static UserProfileDataDto of(Long userId, String username, String preferredCategory,
                                      String requestDescription, Integer budget, String location,
                                      String urgency, List<String> requiredSkills, String projectScale,
                                      String communicationStyle) {
        return UserProfileDataDto.builder()
                .userId(userId)
                .username(username)
                .preferredCategory(preferredCategory)
                .requestDescription(requestDescription)
                .budget(budget)
                .location(location)
                .urgency(urgency)
                .requiredSkills(requiredSkills)
                .projectScale(projectScale)
                .communicationStyle(communicationStyle)
                .build();
    }
    
    /**
     * GPT 프롬프트용 텍스트 생성
     */
    public String toPromptText() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("사용자 정보:\n");
        prompt.append("- 이름: ").append(username).append("\n");
        prompt.append("- 선호 카테고리: ").append(preferredCategory).append("\n");
        prompt.append("- 요청 내용: ").append(requestDescription).append("\n");
        prompt.append("- 예산: ").append(budget).append("원\n");
        prompt.append("- 지역: ").append(location).append("\n");
        prompt.append("- 긴급도: ").append(urgency).append("\n");
        prompt.append("- 필요 기술: ").append(String.join(", ", requiredSkills)).append("\n");
        prompt.append("- 프로젝트 규모: ").append(projectScale).append("\n");
        prompt.append("- 소통 방식: ").append(communicationStyle).append("\n");
        
        return prompt.toString();
    }
}
