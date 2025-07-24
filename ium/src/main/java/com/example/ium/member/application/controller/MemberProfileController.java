package com.example.ium.member.application.controller;

import com.example.ium.member.application.service.ExpertProfileService;
import com.example.ium.member.application.service.MemberProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/profiles")
public class MemberProfileController {

    private final MemberProfileService memberProfileService;
    private final ExpertProfileService expertProfileService;

    @GetMapping("/{memberId}")
    public String getMemberProfile(@PathVariable Long memberId, Model model) {

        model.addAttribute("memberProfile", memberProfileService.getMemberProfile(memberId));

        if (expertProfileService.isExpertProfileActivated(memberId)) {
            return "redirect:/expert-profiles/" + memberId;
        }

        return "member/profile";
    }
}
