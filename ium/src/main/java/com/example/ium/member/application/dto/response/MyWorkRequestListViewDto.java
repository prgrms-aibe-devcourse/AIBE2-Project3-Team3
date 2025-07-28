package com.example.ium.member.application.dto.response;

public record MyWorkRequestListViewDto(
        Long workRequestId,
        String title,
        String content,
        int price,
        String clientName
) {
}
