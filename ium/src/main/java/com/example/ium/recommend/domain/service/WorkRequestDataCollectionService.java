package com.example.ium.recommend.domain.service;

import com.example.ium.workrequest.entity.WorkRequestEntity;
import com.example.ium.workrequest.repository.WorkRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 작업 의뢰 관련 데이터 수집 서비스
 * GPT로 전송할 작업 의뢰 데이터를 수집하고 가공
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WorkRequestDataCollectionService {
    
    private final WorkRequestRepository workRequestRepository;
    
    /**
     * 특정 카테고리의 작업 의뢰들 데이터 수집
     * 
     * @param category 카테고리
     * @return GPT용 작업 의뢰들 데이터 문자열
     */
    public String collectWorkRequestsData(String category) {
        log.debug("작업 의뢰 데이터 수집 시작 - category: {}", category);
        
        // 기존 WorkRequestService의 로직을 활용하여 카테고리별 의뢰 조회
        List<WorkRequestEntity> workRequests;
        if (category == null || category.trim().isEmpty() || "all".equals(category)) {
            workRequests = workRequestRepository.findAllByOrderByAdPointDesc();
        } else {
            String mappedCategory = mapCategory(category);
            workRequests = workRequestRepository.findByCategoryContainingIgnoreCaseOrderByAdPointDesc(mappedCategory);
        }
        
        // OPEN 상태의 의뢰만 필터링
        List<WorkRequestEntity> openRequests = workRequests.stream()
                .filter(request -> request.getStatus() == WorkRequestEntity.Status.OPEN)
                .limit(10) // GPT 토큰 제한을 고려하여 최대 10개로 제한
                .toList();
        
        if (openRequests.isEmpty()) {
            log.warn("카테고리에 해당하는 열린 의뢰가 없습니다: {}", category);
            return "해당 카테고리의 열린 의뢰가 없습니다.";
        }
        
        StringBuilder requestsData = new StringBuilder();
        requestsData.append("열린 작업 의뢰들 목록:\n");
        
        for (WorkRequestEntity request : openRequests) {
            requestsData.append("\n의뢰 ID: ").append(request.getId()).append("\n");
            requestsData.append("- 제목: ").append(request.getTitle()).append("\n");
            requestsData.append("- 내용: ").append(request.getContent()).append("\n");
            requestsData.append("- 카테고리: ").append(request.getCategory()).append("\n");
            requestsData.append("- 가격: ").append(request.getPrice()).append("원\n");
            requestsData.append("- 타입: ").append(request.getType().name()).append("\n");
            requestsData.append("- 광고 포인트: ").append(request.getAdPoint()).append("\n");
            
            if (request.getFileName() != null) {
                requestsData.append("- 첨부파일: ").append(request.getFileName()).append("\n");
            }
        }
        
        log.debug("작업 의뢰 데이터 수집 완료 - category: {}, 의뢰 수: {}", category, openRequests.size());
        return requestsData.toString();
    }
    
    /**
     * 카테고리 매핑 (기존 WorkRequestService의 로직과 동일)
     */
    private String mapCategory(String category) {
        if (category == null || category.trim().isEmpty() || "all".equals(category)) {
            return "";
        }

        return switch (category) {
            case "design" -> "디자인";
            case "programming" -> "프로그래밍";
            case "video" -> "영상";
            case "legal" -> "법무";
            case "translation" -> "번역";
            default -> category;
        };
    }
}
