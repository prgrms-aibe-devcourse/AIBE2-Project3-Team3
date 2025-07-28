package com.example.ium.recommend.infrastructure.client;

import com.example.ium.member.domain.model.expert.ExpertProfile;
import com.example.ium.member.domain.repository.ExpertProfileJPARepository;
import com.example.ium.recommend.application.dto.request.GptRequestDto;
import com.example.ium.recommend.application.dto.response.GptResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * Mock GPT API 클라이언트
 * 개발 및 테스트 환경에서 실제 GPT API 없이 동작하도록 하는 구현체
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Primary
@ConditionalOnProperty(name = "gpt.api.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockGptApiClient implements GptApiClient {
    
    private final Random random = new Random();
    private final ExpertProfileJPARepository expertProfileJPARepository;
    
    @Override
    public GptResponseDto sendRecommendationRequest(GptRequestDto request) throws GptApiException {
        log.info("Mock GPT API 요청 처리 시작 - input 길이: {}", request.getInput().length());
        
        // 실제 API 호출을 시뮬레이션하기 위한 약간의 지연
        try {
            Thread.sleep(500 + random.nextInt(1000)); // 0.5~1.5초 지연
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GptApiException("Mock API 호출 중 인터럽트 발생", e);
        }
        
        // 가끔 실패 시뮬레이션 (5% 확률)
        if (random.nextDouble() < 0.05) {
            throw new GptApiException("Mock API 일시적 오류", 500, "MOCK_ERROR");
        }
        
        // Mock 응답 생성
        String mockResponse = generateMockResponse(request.getInput());
        int tokenCount = mockResponse.length() / 4; // 대략적인 토큰 수 계산
        
        log.info("Mock GPT API 요청 완료 - 응답 길이: {}", mockResponse.length());
        return new GptResponseDto(mockResponse, "stop", tokenCount);
    }
    
    @Override
    public boolean isHealthy() {
        return true; // Mock은 항상 healthy
    }
    
    /**
     * 입력에 따른 Mock 응답 생성
     */
    private String generateMockResponse(String input) {
        String lowercaseInput = input.toLowerCase();
        
        // 사용 가능한 전문가들 조회
        List<ExpertProfile> allExperts = expertProfileJPARepository.findAll().stream()
                .filter(ExpertProfile::isActivated)
                .toList();
        
        log.info("전체 전문가 수: {}, 활성화된 전문가 수: {}", 
                expertProfileJPARepository.findAll().size(), allExperts.size());
        
        if (allExperts.isEmpty()) {
            log.warn("사용 가능한 전문가가 없습니다. 더미 데이터 초기화가 필요합니다.");
            return "죄송합니다. 현재 사용 가능한 전문가가 없습니다. 데이터베이스를 초기화하고 다시 시도해주세요.";
        }
        
        // 랜덤하게 전문가 선택
        ExpertProfile selectedExpert = allExperts.get(random.nextInt(allExperts.size()));
        
        log.info("선택된 전문가: ID={}, 이름={}, 이메일={}", 
                selectedExpert.getMemberId(), 
                selectedExpert.getMember().getUsername(),
                selectedExpert.getMember().getEmail().getValue());
        
        // 사용자 요청에 따른 추천 이유 생성
        String recommendation = generateRecommendationText(input, selectedExpert);
        
        // 우리가 파싱할 수 있는 형식으로 응답 생성
        String response = String.format("""
            EXPERT_ID: %d
            EXPERT_NAME: %s
            EXPERT_EMAIL: %s
            RECOMMENDATION: %s
            """, 
            selectedExpert.getMemberId(),
            selectedExpert.getMember().getUsername(),
            selectedExpert.getMember().getEmail().getValue(),
            recommendation
        );
        
        log.info("Mock GPT 응답 생성 완료:\n{}", response);
        
        return response;
    }
    
    /**
     * 사용자 요청과 전문가 정보를 바탕으로 추천 이유 생성
     */
    private String generateRecommendationText(String userRequest, ExpertProfile expert) {
        String lowercaseRequest = userRequest.toLowerCase();
        String baseRecommendation = String.format("%s 전문가로 %s 분야에서 %d년의 경력을 보유하고 있습니다.", 
                expert.getMember().getUsername(), 
                expert.getMajor() != null ? expert.getMajor() : "다양한",
                java.time.Period.between(expert.getCareerDate().getStartDate(), java.time.LocalDate.now()).getYears());
        
        if (lowercaseRequest.contains("nft") || lowercaseRequest.contains("디지터") || lowercaseRequest.contains("아트")) {
            return baseRecommendation + " 특히 디지털 아트와 NFT 프로젝트에 적합한 경험을 가지고 있어 귀하의 프로젝트에 이상적인 파트너입니다.";
        } else if (lowercaseRequest.contains("웹사이트") || lowercaseRequest.contains("앱")) {
            return baseRecommendation + " 웹/모바일 개발 경험이 풍부하여 안정적이고 현대적인 솔루션을 제공할 수 있습니다.";
        } else if (lowercaseRequest.contains("영상") || lowercaseRequest.contains("편집")) {
            return baseRecommendation + " 영상 콘텐츠 제작과 편집에 뛰어난 실력을 보유하고 있어 고품질의 결과물을 기대할 수 있습니다.";
        } else {
            return baseRecommendation + " 다양한 프로젝트 경험을 바탕으로 최고의 서비스를 제공할 수 있습니다.";
        }
    }
}
