package com.example.ium.workrequest.service;

import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.member.infrastructure.service.FireBaseFileService;
import com.example.ium.money.domain.model.Money;
import com.example.ium.money.domain.model.MoneyType;
import com.example.ium.money.domain.repository.MoneyRepository;
import com.example.ium.workrequest.entity.WorkRequestEntity;
import com.example.ium.workrequest.repository.WorkRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium._core.exception.ErrorCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkRequestService {

    private final WorkRequestRepository workRequestRepository;
    private final MoneyRepository moneyRepository;
    private final MemberJPARepository memberJPARepository;
    private final FireBaseFileService fireBaseFileService;

    // 전체
    public List<WorkRequestEntity> getAllRequests() {
        return workRequestRepository.findAll();
    }
    // 하나
    public WorkRequestEntity getRequest(Long id) {
        return workRequestRepository.findById(id)
                .orElseThrow(() -> new IumApplicationException(ErrorCode.WORK_REQUEST_NOT_FOUND));
    }
    // 새 요청
    public WorkRequestEntity saveRequest(WorkRequestEntity workRequest) {
        return workRequestRepository.save(workRequest);
    }
    public WorkRequestEntity getLatestRequest() {
        return workRequestRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new IllegalArgumentException("등록된 요청이 없습니다."));
    }

    public void uploadFile(MultipartFile file, Long workRequestId, String email) {
        String fileUrl = fireBaseFileService.uploadFile(file, file.getOriginalFilename());

        WorkRequestEntity workRequest = workRequestRepository.findById(workRequestId)
                .orElseThrow(() -> new IumApplicationException(ErrorCode.WORK_REQUEST_NOT_FOUND));
        workRequest.setFileUrl(fileUrl);
        workRequest.setFileName(file.getOriginalFilename());
        workRequest.setStatus(WorkRequestEntity.Status.DONE);
        workRequestRepository.save(workRequest);

        Member member = memberJPARepository.findByEmail(Email.of(email))
                .orElseThrow(() -> new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND));

        Money money = Money.builder()
                .moneyType(workRequest.getType().equals(WorkRequestEntity.Type.FORMAL) ? MoneyType.CREDIT : MoneyType.POINT)
                .price(workRequest.getPrice())
                .member(member)
                .build();
        moneyRepository.save(money);
    }
}