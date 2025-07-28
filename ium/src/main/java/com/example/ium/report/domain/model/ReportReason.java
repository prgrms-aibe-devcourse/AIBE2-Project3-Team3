package com.example.ium.report.domain.model;

import lombok.Getter;

@Getter
public enum ReportReason {

    // 전문가가 의뢰자를 신고할 때
    INAPPROPRIATE_REQUEST("부적절한 의뢰 내용"),
    UNFAIR_COMPENSATION("불공정한 보상 제안"),
    ILLEGAL_CONTENT("불법 또는 정책 위반 요청"),

    // 의뢰자가 전문가를 신고할 때
    LATE_DELIVERY("지연된 작업 전달"),
    LOW_QUALITY("작업 품질 불만"),
    UNPROFESSIONAL_BEHAVIOR("비전문적인 태도"),
    SCAM_ATTEMPT("사기 시도"),

    // 공통
    OFFENSIVE_LANGUAGE("모욕적 언행 / 욕설"),
    NO_RESPONSE("의뢰자 응답 없음"),
    ETC("기타");

    private final String description;

    ReportReason(String description) {
        this.description = description;
    }
}