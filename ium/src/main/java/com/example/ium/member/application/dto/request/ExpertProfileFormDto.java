package com.example.ium.member.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ExpertProfileFormDto {

    private String introduceMessage;
    private String portfolioDescription;
    private String school;
    private String major;
    private String startCareerDate;
    private int salary;
    private boolean negoYn;

    private List<Long> specializationIds; // 전문 분야 ID 목록
}
