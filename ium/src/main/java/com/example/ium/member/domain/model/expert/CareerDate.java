package com.example.ium.member.domain.model.expert;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Embeddable
public class CareerDate {

    @Column(name = "career_start_date", nullable = false)
    private LocalDate startDate;

    protected CareerDate() {}

    private CareerDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public static CareerDate of(LocalDate startDate) {
        return new CareerDate(startDate);
    }

    public String getCareerPeriod() {
        LocalDate now = LocalDate.now();
        Period period = Period.between(startDate, now);
        int years = period.getYears();
        int months = period.getMonths();

        if (years == 0) {
            return months + "개월";
        }
        return years + "년 " + months + "개월";
    }

    public boolean isJunior() {
        LocalDate now = LocalDate.now();
        Period period = Period.between(startDate, now);
        int totalMonths = period.getYears() * 12 + period.getMonths();
        return totalMonths < 12;
    }
}