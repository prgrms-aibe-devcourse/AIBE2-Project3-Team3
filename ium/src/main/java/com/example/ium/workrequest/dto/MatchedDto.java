package com.example.ium.workrequest.dto;

import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Role;
import com.example.ium.member.domain.model.expert.CareerDate;
import com.example.ium.member.domain.model.expert.Salary;

public class MatchedDto {
    private String username;
    private String email;
    private String role;
    private int careerPeriod;
    private int salary;
    private String school;

    public MatchedDto(String username, Email email, Role role, CareerDate careerDate, Salary salary, String school) {
    }
}