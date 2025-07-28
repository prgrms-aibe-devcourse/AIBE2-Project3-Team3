package com.example.ium.member.application.dto.response;

public record WorkRequestInfoViewDto(
        Long workRequestId,
        String title,
        String content,
        int price,
        String expertName,
        String status
) {
}
