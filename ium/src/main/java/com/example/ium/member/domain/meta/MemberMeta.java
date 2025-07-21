package com.example.ium.member.domain.meta;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "member_meta", timeToLive = 24 * 60 * 60) // 1일
public class MemberMeta {

    @Id
    private String id; // Member ID
    private String username;
    private boolean isExpert; // 전문가 여부
    private Long expertProfileId; // 전문가 프로필 ID
    private List<Long> specializationIds; // 전문 분야 ID 목록

    @Builder
    private MemberMeta(String id, String username, boolean isExpert, Long expertProfileId, List<Long> specializationIds) {
        this.id = id;
        this.username = username;
        this.isExpert = isExpert;
        this.expertProfileId = expertProfileId;
        this.specializationIds = specializationIds;
    }

    public static MemberMeta createMemberMeta(Long id, String username, boolean isExpert, Long expertProfileId, List<Long> specializationIds) {
        return MemberMeta.builder()
                .id(String.valueOf(id))
                .username(username)
                .isExpert(isExpert)
                .expertProfileId(expertProfileId)
                .specializationIds(safeSpecializationIds(specializationIds))
                .build();
    }

    private static List<Long> safeSpecializationIds(List<Long> specializationIds) {
        return specializationIds != null ? List.copyOf(specializationIds) : List.of();
    }
}
