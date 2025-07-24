package com.example.ium.member.domain.model.expert;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "expert_specialization_tb")
public class ExpertSpecialization {

    @EmbeddedId
    private ExpertSpecializationId id;

    @Setter
    @MapsId("expertProfileId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_profile_id")
    private ExpertProfile expertProfile; // 전문가 프로필

    @Builder
    private ExpertSpecialization(ExpertSpecializationId id, ExpertProfile expertProfile) {
        this.id = id;
        this.expertProfile = expertProfile;
    }

    public static ExpertSpecialization createExpertSpecialization(ExpertProfile expertProfile, Long specializationId) {
        ExpertSpecializationId id = ExpertSpecializationId.of(expertProfile.getMemberId(), specializationId);
        return ExpertSpecialization.builder()
                .id(id)
                .expertProfile(expertProfile)
                .build();
    }
}
