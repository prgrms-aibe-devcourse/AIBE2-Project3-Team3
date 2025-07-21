package com.example.ium.member.domain.meta;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "member_meta", timeToLive = 24 * 60 * 60) // 1일
public class MemberMeta {

    @Id
    private String email;
    private String username;
    private boolean isExpert;
    private Long expertProfileId;
    private List<Long> specializationIds;

    @Builder
    private MemberMeta(String email, String username, boolean isExpert, Long expertProfileId, List<Long> specializationIds) {
        this.email = email;
        this.username = username;
        this.isExpert = isExpert;
        this.expertProfileId = expertProfileId;
        this.specializationIds = specializationIds;
    }

    public static MemberMeta createMemberMeta(String email, String username, boolean isExpert, Long expertProfileId, List<Long> specializationIds) {
        return MemberMeta.builder()
                .email(email)
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
