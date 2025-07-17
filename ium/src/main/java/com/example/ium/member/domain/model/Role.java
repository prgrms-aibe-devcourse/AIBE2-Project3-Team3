package com.example.ium.member.domain.model;

import lombok.Getter;

@Getter
public enum Role {
    USER("ROLE_USER"),
    EXPERT("ROLE_EXPERT"),
    ADMIN("ROLE_ADMIN");

    private final String key;

    Role(String key) {
        this.key = key;
    }
}
