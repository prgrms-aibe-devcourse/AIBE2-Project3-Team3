package com.example.ium.member.domain.model;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Email {

    @Column(name = "email", nullable = false, unique = true)
    private String value;

    protected Email() {}

    public Email(String value) {
        validateEmail(value);
        this.value = value;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    private static void validateEmail(String value) {
        if (value == null || !value.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            throw new IumApplicationException(ErrorCode.INVALID_REQUEST);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
