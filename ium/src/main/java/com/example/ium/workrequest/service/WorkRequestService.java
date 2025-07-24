package com.example.ium.workrequest.service;

import com.example.ium.workrequest.WorkRequestEntity;
import com.example.ium.workrequest.repository.WorkRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkRequestService {

    private final WorkRequestRepository workRequestRepository;
    // 전체
    public List<WorkRequestEntity> getAllRequests() {
        return workRequestRepository.findAll();
    }
    // 하나
    public WorkRequestEntity getRequest(Long id) {
        return workRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다. id = " + id));
    }
    // 새 요청
    public WorkRequestEntity saveRequest(WorkRequestEntity workRequest) {
        return workRequestRepository.save(workRequest);
    }
    public WorkRequestEntity getLatestRequest() {
        return workRequestRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new IllegalArgumentException("등록된 요청이 없습니다."));
    }
}