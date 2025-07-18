package com.example.ium.member.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberAuthViewController {

    @GetMapping("/signup")
    public String signupPage() {
        return "member/signup";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "member/login";
    }
}
