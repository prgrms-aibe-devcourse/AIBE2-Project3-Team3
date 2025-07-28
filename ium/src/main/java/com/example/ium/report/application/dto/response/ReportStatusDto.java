package com.example.ium.report.application.dto.response;

import com.example.ium.report.domain.model.ReportStatus;

public record ReportStatusDto(
        String code,
        String label
) {
    public static ReportStatusDto from(ReportStatus status) {
        return new ReportStatusDto(status.name(), status.getDescription());
    }
}