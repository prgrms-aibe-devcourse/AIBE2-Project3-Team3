package com.example.ium.specialization.domain.model;

import com.example.ium._core.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "specialization_tb")
public class Specialization extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private SpecializationName specializationName;

    @Builder
    private Specialization(SpecializationName specializationName) {
        this.specializationName = specializationName;
    }

    public static Specialization createSpecialization(String specializationName) {
        return Specialization.builder()
                .specializationName(SpecializationName.of(specializationName))
                .build();
    }
}
