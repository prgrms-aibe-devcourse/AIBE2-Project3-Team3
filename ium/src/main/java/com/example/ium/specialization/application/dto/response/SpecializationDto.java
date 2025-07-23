package com.example.ium.specialization.application.dto.response;

import com.example.ium.specialization.domain.model.Specialization;

public record SpecializationDto(
        Long id,
        String name
) {
    public static SpecializationDto from(Specialization specialization) {
        return new SpecializationDto(specialization.getId(), specialization.getSpecializationName().getValue());
    }
}
