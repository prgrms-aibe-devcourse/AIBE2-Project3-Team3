package com.example.ium.member.domain.model;

import com.example.ium._core.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_tb")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id; // 회원 ID

    @Column(name = "username", nullable = false)
    private String username; // 사용자 이름
    @Embedded
    private Email email; // 이메일
    @Embedded
    private Password password; // 비밀번호

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Builder
    private Member(String username, Email email, Password password, Role role, Status status) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public static Member createMember(String username, Email email, Password password) {
        return Member.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();
    }

    public static Member createExpert(String username, Email email, Password password) {
        return Member.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(Role.EXPERT)
                .status(Status.ACTIVE)
                .build();
    }

    /**
     * 회원의 역할을 전문가로 변경
     * 데이터 초기화나 관리 목적으로만 사용
     */
    public void changeToExpert() {
        this.role = Role.EXPERT;
    }

    /**
     * 회원의 역할을 일반 사용자로 변경
     * 데이터 초기화나 관리 목적으로만 사용
     */
    public void changeToUser() {
        this.role = Role.USER;
    }

    public void deactivate() {
        this.status = Status.SUSPENDED;
    }
}
