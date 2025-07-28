package com.example.ium.recommend.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 추천 전략 팩토리 서비스
 * 사용자 역할에 따라 적절한 추천 전략을 선택하고 관리
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendationStrategyFactory {
    
    private final List<RecommendationStrategy> strategies;
    
    /**
     * 사용자 역할에 맞는 추천 전략 반환
     * 
     * @param role 사용자 역할 (USER/EXPERT)
     * @return 해당하는 추천 전략
     * @throws IllegalArgumentException 지원하지 않는 역할인 경우
     */
    public RecommendationStrategy getStrategy(String role) {
        log.debug("추천 전략 조회 - role: {}", role);
        
        return strategies.stream()
                .filter(strategy -> strategy.supports(role))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("지원하지 않는 역할입니다: {}", role);
                    return new IllegalArgumentException("지원하지 않는 역할입니다: " + role);
                });
    }
    
    /**
     * 지원 가능한 모든 역할 목록 반환
     * 
     * @return 지원 가능한 역할 목록
     */
    public List<String> getSupportedRoles() {
        return List.of("USER", "EXPERT");
    }
}
