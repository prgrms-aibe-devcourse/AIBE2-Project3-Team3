package com.example.ium.report.domain.repository;

import com.example.ium.report.domain.model.ReportStatus;
import com.example.ium.report.domain.model.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReportJpaRepository extends JpaRepository<UserReport, Long> {
    int countByReportedIdAndReportStatus(Long reportedId, ReportStatus reportStatus);
}
