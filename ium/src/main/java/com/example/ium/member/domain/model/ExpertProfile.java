package com.example.ium.member.domain.model;

import com.example.ium._core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "expert_profile_tb")
public class ExpertProfile extends BaseEntity {

    @Id
    private Long memberId;

    @OneToOne
    @MapsId
    private Member member; // 회원 정보

    private boolean activated; // 활성화 여부
}
