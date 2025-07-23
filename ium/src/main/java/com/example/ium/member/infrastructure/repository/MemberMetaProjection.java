package com.example.ium.member.infrastructure.repository;

import com.example.ium.member.domain.model.Email;

public interface MemberMetaProjection {
    Email getEmail();
    String getUsername();
    Long getExpertProfileId();
    Boolean getIsExpert();
    Long getSpecializationId();
}
