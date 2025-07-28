package com.example.ium.report.application.controller;

import com.example.ium._core.security.CustomUserDetails;
import com.example.ium.member.application.dto.request.UserReportFormDto;
import com.example.ium.report.application.service.ReportService;
import com.example.ium.report.domain.model.ReportReason;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/form-data")
    public String createUserReport(@RequestParam("workRequestId") Long workRequestId, Model model) {
        model.addAttribute("workRequestId", workRequestId);
        model.addAttribute("reasons", ReportReason.values());
        return "report/user-report-form";
    }

    @PostMapping
    public String createUserReport(@ModelAttribute UserReportFormDto requestDto, @AuthenticationPrincipal CustomUserDetails memberDetails) {
        Long memberId = memberDetails.getMemberId();
        reportService.createUserReport(requestDto, memberId);
        return "redirect:/workrequest/" + requestDto.workRequestId();
    }
}
