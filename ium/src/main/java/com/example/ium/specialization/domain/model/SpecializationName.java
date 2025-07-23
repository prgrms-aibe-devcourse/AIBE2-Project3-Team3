package com.example.ium.specialization.domain.model;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class SpecializationName {

    @Column(name = "specialization_name", nullable = false, unique = true)
    private String value;

    protected SpecializationName() {}

    private SpecializationName(String value) {
        this.value = value;
    }

    public static SpecializationName of(String value) {
        validateSpecializationName(value);
        return new SpecializationName(value);
    }

    private static void validateSpecializationName(String value) {
        if (value == null || value.isBlank()) {
            throw new IumApplicationException(ErrorCode.INVALID_REQUEST);
        }
    }
}
