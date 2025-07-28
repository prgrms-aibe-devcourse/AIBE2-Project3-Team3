package com.example.ium.member.application.controller;

import com.example.ium._core.security.CustomUserDetails;
import com.example.ium.member.application.service.ExpertProfileService;
import com.example.ium.member.application.service.MemberProfileService;
import com.example.ium.money.service.MoneyService;
import com.example.ium.workrequest.service.WorkRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/profiles")
public class MemberProfileController {

    private final MemberProfileService memberProfileService;
    private final ExpertProfileService expertProfileService;
    private final MoneyService moneyService;
    private final WorkRequestService workRequestService;

    @GetMapping
    public String getMemberProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        Long memberId = userDetails.getMemberId();

        if (expertProfileService.isExpertProfileActivated(memberId)) {
            return "redirect:/expert-profiles/" + memberId;
        }

        model.addAttribute("memberProfile", memberProfileService.getMemberProfile(memberId));
        model.addAttribute("moneyInfo", moneyService.getMoneyInfo(memberId));
        model.addAttribute("workRequestInfo", workRequestService.getWorkRequestInfo(userDetails.getUsername()));
        return "member/profile";
    }
}
