package com.example.ium.member.domain.model.expert;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class NegoYn {

    @Column(name = "nego_yn", nullable = false, length = 1)
    private String value;

    protected NegoYn() {}

    private NegoYn(String value) {
        this.value = value;
    }

    public static NegoYn of(boolean isNegotiable) {
        return isNegotiable ? yes() : no();
    }

    public static NegoYn yes() {
        return new NegoYn("Y");
    }

    public static NegoYn no() {
        return new NegoYn("N");
    }

    public boolean isNegotiable() {
        return "Y".equals(value);
    }
}
