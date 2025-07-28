package com.example.ium.member.application.dto.response;

public record UserReportViewDto(
    Long reportId, // 신고 ID
    String reporterEmail, // 신고자 이메일
    String reportedEmail, // 피신고자 이메일
    String reportReason, // 신고 사유
    String reportDetails, // 신고 상세 내용
    String reportStatus // 신고 상태
) {
}
