package com.example.ium.workrequest.dto;

import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.model.Role;
import com.example.ium.member.domain.model.expert.CareerDate;
import com.example.ium.member.domain.model.expert.ExpertProfile;
import com.example.ium.member.domain.model.expert.Salary;

public class MatchedDto {
    private String username;
    private String email;
    private String role;
    private String careerPeriod;
    private int salary;
    private String school;

    public MatchedDto(String username, Email email, Role role, CareerDate careerDate, Salary salary, String school) {
        this.username = username;
        this.email = email.getValue();
        this.role = role.name();
        this.careerPeriod = careerDate.getCareerPeriod();
        this.salary = salary.getValue();
        this.school = school;
    }

    public static MatchedDto of(ExpertProfile expertProfile) {
        Member member = expertProfile.getMember();

        return new MatchedDto(
                member.getUsername(),
                member.getEmail(),
                member.getRole(),
                expertProfile.getCareerDate(),
                expertProfile.getSalary(),
                expertProfile.getSchool()
        );
    }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getCareerPeriod() { return careerPeriod; }
    public int getSalary() { return salary; }
    public String getSchool() { return school; }
}