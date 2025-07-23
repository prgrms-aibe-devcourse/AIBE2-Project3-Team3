package com.example.ium.member.domain.model.expert;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class CompletedRequestCount {

    @Column(name = "completed_request_count", nullable = false)
    private int value;

    protected CompletedRequestCount() {
        this.value = 0; // 기본값 설정
    }

    private CompletedRequestCount(int value) {
        this.value = value;
    }

    public static CompletedRequestCount init() {
        return new CompletedRequestCount(0);
    }

    public CompletedRequestCount increment() {
        this.value = this.value + 1;
        return this;
    }
}
