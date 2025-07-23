package com.example.ium.member.application.dto.mapper;

import com.example.ium.member.domain.meta.MemberMeta;
import com.example.ium.member.infrastructure.repository.MemberMetaProjection;

import java.util.List;
import java.util.Objects;

public class MemberMetaMapper {
    public static MemberMeta toMemberMeta(List<MemberMetaProjection> rows) {
        if (rows == null || rows.isEmpty()) {
            return null;
        }

        MemberMetaProjection firstRow = rows.get(0);

        if (firstRow.getExpertProfileId() == null) {
            return null;
        }

        List<Long> specializationIds = rows.stream()
                .map(MemberMetaProjection::getSpecializationId)
                .filter(Objects::nonNull)
                .toList();

        return MemberMeta.createMemberMeta(
                firstRow.getEmail().getValue(),
                firstRow.getUsername(),
                Boolean.TRUE.equals(firstRow.getIsExpert()),
                firstRow.getExpertProfileId(),
                specializationIds
        );
    }
}
