package com.example.ium.member.application.controller;

import com.example.ium._core.security.CustomUserDetails;
import com.example.ium.member.application.dto.request.ExpertProfileFormDto;
import com.example.ium.member.application.service.ExpertProfileService;
import com.example.ium.specialization.application.dto.response.SpecializationDto;
import com.example.ium.specialization.application.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/expert-profiles")
public class ExpertProfileController {

    private final ExpertProfileService expertProfileService;
    private final SpecializationService specializationService;

    /**
     * 전문가 프로필 페이지로 이동
     * @return 전문가 프로필 페이지 경로
     */
    @GetMapping("/form-data")
    public String createExpertProfile(Model model) {
        List<SpecializationDto> specializations = specializationService.getAllSpecializations();
        model.addAttribute("specializations", specializations);
        model.addAttribute("expertProfileForm", new ExpertProfileFormDto());
        return "member/create-expert-profile"; // 전문가 프로필 작성 페이지 경로
    }

    /**
     * 전문가 프로필 작성
     *
     */
    @PostMapping
    public String createExpertProfile(@ModelAttribute ExpertProfileFormDto requestDto, @AuthenticationPrincipal CustomUserDetails memberDetails) {
        Long memberId = memberDetails.getMemberId();
        expertProfileService.createExpertProfile(requestDto, memberId);
        return "redirect:/profile/" + memberId;
    }

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
