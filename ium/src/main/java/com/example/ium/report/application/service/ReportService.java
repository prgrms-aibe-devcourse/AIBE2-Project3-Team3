package com.example.ium.report.application.service;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium.member.application.dto.request.UserReportFormDto;
import com.example.ium.member.application.dto.response.UserReportViewDto;
import com.example.ium.member.application.service.MemberAuthService;
import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.report.application.dto.response.ReportStatusDto;
import com.example.ium.report.domain.model.ReportStatus;
import com.example.ium.report.domain.model.UserReport;
import com.example.ium.report.domain.repository.UserReportJpaRepository;
import com.example.ium.workrequest.entity.WorkRequestEntity;
import com.example.ium.workrequest.repository.WorkRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportService {

    private final UserReportJpaRepository userReportJpaRepository;
    private final WorkRequestRepository workRequestRepository;
    private final MemberJPARepository memberJPARepository;
    private final MemberAuthService memberAuthService;

    @Transactional
    public void createUserReport(UserReportFormDto requestDto, Long memberId) {
        WorkRequestEntity workRequest = workRequestRepository.findById(requestDto.workRequestId())
                .orElseThrow(() -> new IumApplicationException(ErrorCode.WORK_REQUEST_NOT_FOUND));

        Member client = memberJPARepository.findByEmail(Email.of(workRequest.getCreatedBy()))
                .orElseThrow(() -> new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND));

        Long reporterId;
        Long reportedId;

        if (memberId.equals(client.getId())) {
            reporterId = memberId;
            if (workRequest.getExpert() == null) {
                throw new IumApplicationException(ErrorCode.WORK_REQUEST_DOES_NOT_HAVE_EXPERT);
            }
            reportedId = workRequest.getExpert();
        } else {
            reporterId = memberId;
            reportedId = client.getId();
        }

        UserReport userReport = UserReport.createReport(
                reporterId,
                reportedId,
                requestDto.reportReason(),
                requestDto.reportDetail()
        );

        userReportJpaRepository.save(userReport);
    }

    public List<UserReportViewDto> getUserReportList() {
        List<UserReport> userReports = userReportJpaRepository.findAll();

        Set<Long> memberIds = userReports.stream()
                .flatMap(report -> Stream.of(report.getReporterId(), report.getReportedId()))
                .collect(Collectors.toSet());

        Map<Long, Member> memberMap = memberJPARepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, m -> m));

        return userReports.stream()
                .map(report -> {
                    Member reporter = memberMap.get(report.getReporterId());
                    Member reported = memberMap.get(report.getReportedId());

                    if (reporter == null || reported == null) {
                        throw new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND);
                    }

                    return new UserReportViewDto(
                            report.getId(),
                            reporter.getEmail().getValue(),
                            reported.getEmail().getValue(),
                            report.getReportReason().name(),
                            report.getReportDetails(),
                            report.getReportStatus().name()
                    );
                })
                .toList();
    }

    public List<ReportStatusDto> getReportStatusList() {
        return Arrays.stream(ReportStatus.values())
                .map(ReportStatusDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserReportStatus(Long reportId, ReportStatus status) {
        UserReport userReport = userReportJpaRepository.findById(reportId)
                .orElseThrow(() -> new IumApplicationException(ErrorCode.REPORT_NOT_FOUND));
        userReport.updateReportStatus(status);
        userReportJpaRepository.save(userReport);

        if (userReportJpaRepository.countByReportedIdAndReportStatus(userReport.getReportedId(), ReportStatus.RESOLVED) >= 1) {
            memberAuthService.suspendUser(userReport.getReportedId());
        }
    }
}
