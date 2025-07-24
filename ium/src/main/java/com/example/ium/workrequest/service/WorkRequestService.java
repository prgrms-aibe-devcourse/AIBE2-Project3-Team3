package com.example.ium.workrequest.service;

import com.example.ium.workrequest.entity.WorkRequestEntity;
import com.example.ium.workrequest.repository.WorkRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium._core.exception.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkRequestService {

    private final WorkRequestRepository workRequestRepository;
    
    // 전체 (ad_point 높은 순으로 정렬)
    public List<WorkRequestEntity> getAllRequests() {
        return workRequestRepository.findAllByOrderByAdPointDesc();
    }
    
    // 카테고리별 필터링
    public List<WorkRequestEntity> getRequestsByCategory(String category) {
        // 카테고리 매핑
        String mappedCategory = mapCategory(category);
        return workRequestRepository.findByCategoryContainingIgnoreCaseOrderByAdPointDesc(mappedCategory);
    }
    
    // 검색 기능
    public List<WorkRequestEntity> searchRequests(String search) {
        return workRequestRepository.findBySearchTermOrderByAdPointDesc(search);
    }
    
    // 카테고리와 검색 조건 모두 적용
    public List<WorkRequestEntity> getRequestsByCategoryAndSearch(String category, String search) {
        String mappedCategory = mapCategory(category);
        return workRequestRepository.findByCategoryAndSearchOrderByAdPointDesc(mappedCategory, search);
    }
    
    // 카테고리 매핑 메소드
    private String mapCategory(String category) {
        if (category == null || category.trim().isEmpty() || "all".equals(category)) {
            return "";
        }
        
        switch (category) {
            case "design":
                return "디자인";
            case "programming":
                return "프로그래밍";
            case "video":
                return "영상";
            case "legal":
                return "법무";
            case "translation":
                return "번역";
            default:
                return category;
        }
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
}