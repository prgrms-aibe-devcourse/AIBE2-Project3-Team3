package com.example.ium.member.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Salary {
    @Column(name = "salary", nullable = false)
    private Integer value;

    protected Salary() {}

    private Salary(Integer value) {
        this.value = value;
    }

    public static Salary of(Integer value) {
        return new Salary(value);
    }
}
