package com.example.ium.member.application.dto.request;

import com.example.ium.report.domain.model.ReportReason;

public record UserReportFormDto(
        Long workRequestId,
        ReportReason reportReason,
        String reportDetail
) {
}
