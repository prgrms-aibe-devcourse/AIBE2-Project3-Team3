package com.example.ium.member.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.time.YearMonth;

@Getter
@Embeddable
public class CareerDate {

    @Column(name = "career_start_date", nullable = false)
    private YearMonth startDate;

    protected CareerDate() {}

    private CareerDate(YearMonth startDate) {
        this.startDate = startDate;
    }

    public static CareerDate of(String startDate) {
        return new CareerDate(YearMonth.parse(startDate));
    }

    public String getCareerPeriod() {
        YearMonth now = YearMonth.now();
        int totalMonths = (now.getYear() - startDate.getYear()) * 12 + (now.getMonthValue() - startDate.getMonthValue());

        int years = totalMonths / 12;
        int months = totalMonths % 12;

        if (years == 0) {
            return months + "개월";
        }
        return years + "년 " + months + "개월";
    }

    public boolean isJunior() {
        YearMonth now = YearMonth.now();
        int totalMonths = (now.getYear() - startDate.getYear()) * 12 + (now.getMonthValue() - startDate.getMonthValue());
        return totalMonths < 12;
    }
}
