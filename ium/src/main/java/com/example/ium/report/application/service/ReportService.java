package com.example.ium.report.application.service;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium.member.application.dto.request.UserReportFormDto;
import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.report.domain.model.UserReport;
import com.example.ium.report.domain.repository.UserReportJpaRepository;
import com.example.ium.workrequest.entity.WorkRequestEntity;
import com.example.ium.workrequest.repository.WorkRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportService {

    private final UserReportJpaRepository userReportJpaRepository;
    private final WorkRequestRepository workRequestRepository;
    private final MemberJPARepository memberJPARepository;

    public void createUserReport(UserReportFormDto requestDto, Long memberId) {
        WorkRequestEntity workRequest = workRequestRepository.findById(requestDto.workRequestId())
                .orElseThrow(() -> new IumApplicationException(ErrorCode.WORK_REQUEST_NOT_FOUND));

        Member client = memberJPARepository.findByEmail(Email.of(workRequest.getCreatedBy()))
                .orElseThrow(() -> new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND));

        Long reporterId;
        Long reportedId;

        if (memberId.equals(client.getId())) {
            reporterId = memberId;
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
}
