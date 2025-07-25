package com.example.ium.member.application.dto.response;

public record MyWorkRequestListViewDto(
        Long workRequestId,
        String title,
        int price,
        String clientName
) {
}
