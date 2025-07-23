package com.example.ium.member.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ExpertProfileFormDto {

    private String introduceMessage;
    private String portfolioDescription;
    private String school;
    private String major;
    private LocalDate startCareerDate;
    private int salary;
    private boolean negoYn;

    private List<Long> specializationIds; // 전문 분야 ID 목록
}
