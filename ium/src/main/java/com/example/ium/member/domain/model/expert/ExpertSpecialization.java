package com.example.ium.member.domain.model.expert;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_specialization_tb")
public class ExpertSpecialization {

    @EmbeddedId
    private ExpertSpecializationId id;

    @MapsId("expertProfileId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_profile_id")
    private ExpertProfile expertProfile; // 전문가 프로필

    @Column(name = "specialization_id", nullable = false, insertable = false, updatable = false)
    private Long specializationId; // 전문 분야 ID

    @Builder
    private ExpertSpecialization(ExpertProfile expertProfile, Long specializationId) {
        this.id = ExpertSpecializationId.of(expertProfile.getMemberId(), specializationId);
        this.expertProfile = expertProfile;
        this.specializationId = specializationId;
    }

    public static ExpertSpecialization createExpertSpecialization(ExpertProfile expertProfile, Long specializationId) {
        return ExpertSpecialization.builder()
                .expertProfile(expertProfile)
                .specializationId(specializationId)
                .build();
    }
}
