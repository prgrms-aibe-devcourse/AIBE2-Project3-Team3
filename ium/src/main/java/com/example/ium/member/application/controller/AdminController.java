package com.example.ium.member.application.controller;

import com.example.ium.report.application.service.ReportService;
import com.example.ium.report.domain.model.ReportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        model.addAttribute("reportStatusList", reportService.getReportStatusList()); // 신고 상태
        return "member/admin/report-management"; // 관리자 신고 관리 페이지 경로
    }

    @PostMapping("/reports/update")
    public String processUserReport(@RequestParam("reportId") Long reportId,
                                    @RequestParam("status") String status) {
        log.info("Updating report with id {} and status {}", reportId, status);
        reportService.updateUserReportStatus(reportId, ReportStatus.valueOf(status));
        return "redirect:/admin/report-management";
    }
}
