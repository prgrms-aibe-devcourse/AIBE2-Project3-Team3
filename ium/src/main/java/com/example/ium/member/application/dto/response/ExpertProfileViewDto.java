package com.example.ium.member.application.dto.response;

import java.time.LocalDate;
import java.util.List;

public record ExpertProfileViewDto(
        Long memberId,
        String introduceMessage,
        String portfolioDescription,
        String school,
        String major,
        LocalDate startCareerDate,
        int salary,
        String negoYn,
        int completedRequestCount,
        List<SpecializationSummary> specializations,
        List<AttachmentInfo> attachments
) {
    public record SpecializationSummary(Long id, String name) {}
    public record AttachmentInfo(String fileName, String fileUrl, String fileType) {}
}