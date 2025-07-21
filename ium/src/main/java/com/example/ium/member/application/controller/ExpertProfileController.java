package com.example.ium.member.application.controller;

import com.example.ium.member.application.service.ExpertProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/profile")
public class ExpertProfileController {

    private final ExpertProfileService expertProfileService;

    /**
     * 전문가 프로필 활성화
     * @param memberId 전문가 프로필의 멤버 ID
     */
    @PatchMapping("/{memberId}/activate")
    public String activateExpertProfile(@PathVariable("memberId") Long memberId) {
        expertProfileService.activateExpertProfile(memberId);
        return "redirect:/profile/" + memberId; // 프로필 페이지로 리다이렉트
    }

    /**
     * 전문가 프로필 비활성화
     * @param memberId 전문가 프로필의 멤버 ID
     */
    @PatchMapping("/{memberId}/deactivate")
    public String deactivateExpertProfile(@PathVariable("memberId") Long memberId) {
        expertProfileService.deactivateProfile(memberId);
        return "redirect:/profile/" + memberId; // 프로필 페이지로 리다이렉트
    }
}
