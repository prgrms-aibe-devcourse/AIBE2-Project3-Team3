package com.example.ium.member.application.controller;

import com.example.ium.report.application.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ReportService reportService;

    // 관리자 대시보드 페이지로 이동
    @RequestMapping("/dashboard")
    public String adminDashboard() {
        return "member/admin/dashboard"; // 관리자 대시보드 페이지 경로
    }

    // 관리자 회원 관리 페이지로 이동
    @RequestMapping("/member-management")
    public String memberManagement() {
        return "member/admin/member-management"; // 관리자 회원 관리 페이지 경로
    }

    // 관리자 신고 관리 페이지로 이동
    @RequestMapping("/report-management")
    public String reportManagement(Model model) {
        model.addAttribute("userReportList", reportService.getUserReportList()); // 신고 목록을 모델에 추가
        return "member/admin/report-management"; // 관리자 신고 관리 페이지 경로
    }
}
