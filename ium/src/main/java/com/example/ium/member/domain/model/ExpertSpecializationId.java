package com.example.ium.member.domain.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class ExpertSpecializationId implements Serializable {

    private Long expertProfileId;
    private Long specializationId;

    protected ExpertSpecializationId() {}

    private ExpertSpecializationId(Long expertProfileId, Long specializationId) {
        this.expertProfileId = expertProfileId;
        this.specializationId = specializationId;
    }

    public static ExpertSpecializationId of(Long expertProfileId, Long specializationId) {
        return new ExpertSpecializationId(expertProfileId, specializationId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpertSpecializationId that)) return false;
        return Objects.equals(expertProfileId, that.expertProfileId) &&
                Objects.equals(specializationId, that.specializationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expertProfileId, specializationId);
    }
}
