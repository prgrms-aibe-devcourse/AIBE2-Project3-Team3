package com.example.ium.report.domain.model;

import com.example.ium._core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_report_tb")
public class UserReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id; // 신고 ID

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId; // 신고자 ID
    @Column(name = "reported_id", nullable = false)
    private Long reportedId; // 피신고자 ID

    @Column(name = "report_reason", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportReason reportReason; // 신고 사유
    @Column(name = "report_details")
    private String reportDetails; // 신고 상세 내용
    @Column(name = "report_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus; // 신고 상태

    @Builder
    private UserReport(Long reporterId, Long reportedId, ReportReason reportReason, String reportDetails, ReportStatus reportStatus) {
        this.reporterId = reporterId;
        this.reportedId = reportedId;
        this.reportReason = reportReason;
        this.reportDetails = reportDetails;
        this.reportStatus = reportStatus;
    }

    public static UserReport createReport(Long reporterId, Long reportedId, ReportReason reportReason, String reportDetails) {
        return UserReport.builder()
                .reporterId(reporterId)
                .reportedId(reportedId)
                .reportReason(reportReason)
                .reportDetails(reportDetails)
                .reportStatus(ReportStatus.PENDING) // 기본 상태는 PENDING
                .build();
    }
}
