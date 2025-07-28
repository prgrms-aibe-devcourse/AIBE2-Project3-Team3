package com.example.ium.recommend.application.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * GPT로 보낼 전문가 정보 DTO
 * EXPERT에게 의뢰를 추천할 때 사용
 */
@Getter
@Builder
public class ExpertProfileDataDto {
    
    private Long expertId;              // 전문가 ID
    private String expertName;          // 전문가 이름
    private String introduceMessage;    // 소개글
    private List<String> specializations; // 전문 분야 목록
    private String school;              // 학교
    private String major;               // 전공
    private LocalDate careerStartDate;  // 경력 시작일
    private Integer salary;             // 희망 연봉
    private Boolean negoYn;             // 협상 가능 여부
    private Integer completedRequestCount; // 완료한 의뢰 수
    private String portfolioDescription; // 포트폴리오 설명
    private List<String> skills;        // 보유 기술
    private String workStyle;           // 작업 스타일
    private String availableLocation;   // 작업 가능 지역
    
    public static ExpertProfileDataDto of(Long expertId, String expertName, String introduceMessage,
                                        List<String> specializations, String school, String major,
                                        LocalDate careerStartDate, Integer salary, Boolean negoYn,
                                        Integer completedRequestCount, String portfolioDescription,
                                        List<String> skills, String workStyle, String availableLocation) {
        return ExpertProfileDataDto.builder()
                .expertId(expertId)
                .expertName(expertName)
                .introduceMessage(introduceMessage)
                .specializations(specializations)
                .school(school)
                .major(major)
                .careerStartDate(careerStartDate)
                .salary(salary)
                .negoYn(negoYn)
                .completedRequestCount(completedRequestCount)
                .portfolioDescription(portfolioDescription)
                .skills(skills)
                .workStyle(workStyle)
                .availableLocation(availableLocation)
                .build();
    }
    
    /**
     * GPT 프롬프트용 텍스트 생성
     */
    public String toPromptText() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("전문가 정보:\n");
        prompt.append("- 이름: ").append(expertName).append("\n");
        prompt.append("- 소개: ").append(introduceMessage).append("\n");
        prompt.append("- 전문분야: ").append(String.join(", ", specializations)).append("\n");
        prompt.append("- 학력: ").append(school).append(" ").append(major).append("\n");
        prompt.append("- 경력: ").append(getCareerYears()).append("년\n");
        prompt.append("- 희망 연봉: ").append(salary).append("원\n");
        prompt.append("- 협상 가능: ").append(negoYn ? "가능" : "불가능").append("\n");
        prompt.append("- 완료 의뢰: ").append(completedRequestCount).append("건\n");
        prompt.append("- 포트폴리오: ").append(portfolioDescription).append("\n");
        prompt.append("- 보유 기술: ").append(String.join(", ", skills)).append("\n");
        prompt.append("- 작업 스타일: ").append(workStyle).append("\n");
        prompt.append("- 작업 지역: ").append(availableLocation).append("\n");
        
        return prompt.toString();
    }
    
    /**
     * 경력 년수 계산
     */
    private int getCareerYears() {
        if (careerStartDate == null) return 0;
        return LocalDate.now().getYear() - careerStartDate.getYear();
    }
}
