package com.example.ium.workrequest.dto;

public record WorkRequestDto(
        String category,
        String title,
        int price,
        String name,
        String status,
        String description
) {
}