package com.example.ium.member.application.controller;

import com.example.ium._core.security.CustomUserDetails;
import com.example.ium.member.application.dto.request.ExpertProfileFormDto;
import com.example.ium.member.application.service.ExpertProfileService;
import com.example.ium.money.service.MoneyService;
import com.example.ium.specialization.application.dto.response.SpecializationDto;
import com.example.ium.specialization.application.service.SpecializationService;
import com.example.ium.workrequest.service.WorkRequestService;
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
    private final WorkRequestService workRequestService;
    private final MoneyService moneyService;

    /**
     * 전문가 프로필 작성 페이지로 이동
     * @return 전문가 프로필 작성 페이지 경로
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
        return "redirect:/profiles";
    }

    /**
     * 전문가 프로필 활성화
     * @param memberId 전문가 프로필의 멤버 ID
     */
    @PatchMapping("/{memberId}/activate")
    public String activateExpertProfile(@PathVariable("memberId") Long memberId) {
        if (!expertProfileService.isExpertProfileActivated(memberId)) {
            return "redirect:/expert-profiles/form-data"; // 프로필 작성 페이지로 리다이렉트
        }
        expertProfileService.activateExpertProfile(memberId);
        return "redirect:/expert-profiles/" + memberId;
    }

    /**
     * 전문가 프로필 비활성화
     * @param memberId 전문가 프로필의 멤버 ID
     */
    @PatchMapping("/{memberId}/deactivate")
    public String deactivateExpertProfile(@PathVariable("memberId") Long memberId) {
        expertProfileService.deactivateProfile(memberId);
        return "redirect:/profiles";
    }

    /**
     * 전문가 프로필 조회
     * @param memberId 전문가 프로필의 멤버 ID
     * @param model 모델 객체
     * @return 전문가 프로필 페이지 경로
     */
    @GetMapping("/{memberId}")
    public String getExpertProfile(@PathVariable Long memberId, Model model) {

        if (!expertProfileService.isExpertProfileActivated(memberId)) {
            return "redirect:/expert-profiles/form-data"; // 프로필 작성 페이지로 리다이렉트
        }
        model.addAttribute("expertProfile", expertProfileService.getExpertProfile(memberId));
        model.addAttribute("moneyInfo", moneyService.getMoneyInfo(memberId));
        return "member/expert-profile"; // 전문가 프로필 페이지 경로
    }

    @GetMapping("/{memberId}/work-requests")
    public String getMyWorkRequests(@PathVariable Long memberId, Model model) {
        if (!expertProfileService.isExpertProfileActivated(memberId)) {
            return "redirect:/expert-profiles/form-data"; // 프로필 작성 페이지로 리다이렉트
        }
        model.addAttribute("countWorkRequestsByStatus", workRequestService.countMyWorkRequestsByStatus(memberId));
        model.addAttribute("moneyInfo", moneyService.getMoneyInfo(memberId));
        return "member/work-request"; // 전문가 프로필 페이지 경로
    }
}
