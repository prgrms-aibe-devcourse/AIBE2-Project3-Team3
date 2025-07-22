package com.example.ium.workrequest.dto;

public record ExpertDto(
        String name,
        String specialty,
        int price,
        String category,
        String image
) {
}