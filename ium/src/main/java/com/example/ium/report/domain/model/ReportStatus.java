package com.example.ium.report.domain.model;

import lombok.Getter;

@Getter
public enum ReportStatus {

    PENDING("접수됨"),
    UNDER_REVIEW("검토 중"),
    RESOLVED("조치 완료"),
    REJECTED("기각됨");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

}