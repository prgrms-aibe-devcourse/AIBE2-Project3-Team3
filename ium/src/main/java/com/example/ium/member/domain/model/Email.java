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
    private String email;

    protected Email() {}
    public Email(String email) {
        validateEmail(email);
        this.email = email;
    }

    private static void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            throw new IumApplicationException(ErrorCode.INVALID_REQUEST);
        }
    }
}
