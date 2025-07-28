package com.example.ium.member.application.dto.response;

public record MemberProfileViewDto(
        Long memberId,
        String username,
        String email
) {
}
