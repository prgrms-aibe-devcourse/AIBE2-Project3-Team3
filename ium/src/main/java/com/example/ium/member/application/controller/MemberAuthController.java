package com.example.ium.member.application.controller;

import com.example.ium.member.application.dto.request.SignUpRequestDto;
import com.example.ium.member.application.service.MemberAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/api/auth")
public class MemberAuthController {

    private final MemberAuthService memberAuthService;

    /**
     * 회원 가입 페이지로 이동
     * @return 로그인 페이지 경로
     */
    @PostMapping("/signup")
    public String signUp(@Valid @ModelAttribute SignUpRequestDto requestDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.error("회원 가입 요청에서 오류 발생: {}", bindingResult.getAllErrors());
            return "redirect:/signup?error"; // 에러가 있을 경우 회원 가입 페이지로 리다이렉트
        }

        memberAuthService.signUp(requestDto);
        return "redirect:/login"; // 회원 가입 후 로그인 페이지로 리다이렉트
    }
}
