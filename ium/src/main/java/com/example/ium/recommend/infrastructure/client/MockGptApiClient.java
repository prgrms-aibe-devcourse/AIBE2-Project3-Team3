package com.example.ium.recommend.infrastructure.client;

import com.example.ium.recommend.application.dto.request.GptRequestDto;
import com.example.ium.recommend.application.dto.response.GptResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Mock GPT API 클라이언트
 * 개발 및 테스트 환경에서 실제 GPT API 없이 동작하도록 하는 구현체
 */
@Slf4j
@Component
@Primary
@ConditionalOnProperty(name = "gpt.api.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockGptApiClient implements GptApiClient {
    
    private final Random random = new Random();
    
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
        
        if (lowercaseInput.contains("디자인") || lowercaseInput.contains("design")) {
            return generateDesignMockResponse(input);
        } else if (lowercaseInput.contains("프로그래밍") || lowercaseInput.contains("개발") || lowercaseInput.contains("programming")) {
            return generateProgrammingMockResponse(input);
        } else if (lowercaseInput.contains("영상") || lowercaseInput.contains("video")) {
            return generateVideoMockResponse(input);
        } else if (lowercaseInput.contains("세무") || lowercaseInput.contains("법무") || lowercaseInput.contains("legal")) {
            return generateLegalMockResponse(input);
        } else if (lowercaseInput.contains("번역") || lowercaseInput.contains("통역") || lowercaseInput.contains("translation")) {
            return generateTranslationMockResponse(input);
        } else {
            return generateGeneralMockResponse(input);
        }
    }
    
    private String generateDesignMockResponse(String input) {
        return """
            🎨 디자인 분야 추천 분석 결과
            
            입력하신 요구사항을 분석한 결과, 다음과 같은 전문가들을 추천드립니다:
            
            **추천 전문가 1: 김창의 디자이너**
            - 전문분야: UI/UX 디자인, 브랜딩
            - 경력: 5년
            - 추천 이유: 사용자 중심의 직관적인 디자인을 전문으로 하며, 특히 스타트업 브랜딩에 뛰어난 성과를 보이고 있습니다.
            - 예상 비용: 80-120만원
            - 매칭도: 92%
            
            **추천 전문가 2: 박미학 디자이너**
            - 전문분야: 그래픽 디자인, 패키지 디자인
            - 경력: 7년
            - 추천 이유: 브랜드 아이덴티티 구축에 탁월하며, 다양한 업종의 로고 및 브랜딩 프로젝트 경험이 풍부합니다.
            - 예상 비용: 60-100만원
            - 매칭도: 88%
            
            이 추천은 귀하의 프로젝트 요구사항과 예산, 일정을 종합적으로 고려하여 생성되었습니다.
            """;
    }
    
    private String generateProgrammingMockResponse(String input) {
        return """
            💻 프로그래밍 분야 추천 분석 결과
            
            기술 요구사항을 분석한 결과, 다음 전문가들이 가장 적합합니다:
            
            **추천 전문가 1: 이테크 개발자**
            - 전문분야: 풀스택 웹 개발 (React, Node.js, PostgreSQL)
            - 경력: 6년
            - 추천 이유: 현대적인 웹 기술 스택에 능숙하며, 확장 가능한 아키텍처 설계 경험이 풍부합니다.
            - 예상 비용: 150-250만원
            - 매칭도: 95%
            
            **추천 전문가 2: 박솔루션 개발자**
            - 전문분야: 백엔드 개발 (Spring Boot, AWS)
            - 경력: 4년
            - 추천 이유: 안정적이고 성능 최적화된 서버 개발을 전문으로 하며, 클라우드 인프라 구축 경험이 뛰어납니다.
            - 예상 비용: 120-180만원
            - 매칭도: 89%
            
            프로젝트의 복잡도와 요구사항을 고려한 맞춤형 추천입니다.
            """;
    }
    
    private String generateVideoMockResponse(String input) {
        return """
            🎬 영상편집 분야 추천 분석 결과
            
            영상 프로젝트 요구사항에 따른 전문가 추천:
            
            **추천 전문가 1: 김영상 에디터**
            - 전문분야: 유튜브 콘텐츠 편집, 모션 그래픽
            - 경력: 4년
            - 추천 이유: 트렌디한 편집 스타일과 높은 완성도로 다수의 인기 유튜버와 협업 경험이 있습니다.
            - 예상 비용: 편당 15-25만원
            - 매칭도: 91%
            
            **추천 전문가 2: 박크리에이티브 에디터**
            - 전문분야: 홍보영상, 브랜드 필름 제작
            - 경력: 8년
            - 추천 이유: 기업 홍보영상 제작에 특화되어 있으며, 스토리텔링과 브랜딩이 결합된 영상 제작이 뛰어납니다.
            - 예상 비용: 200-400만원
            - 매칭도: 87%
            
            영상의 목적과 타겟 오디언스를 고려한 최적의 매칭입니다.
            """;
    }
    
    private String generateLegalMockResponse(String input) {
        return """
            ⚖️ 세무/법무/노무 분야 추천 분석 결과
            
            법무 관련 요구사항에 따른 전문가 추천:
            
            **추천 전문가 1: 이법무 세무사**
            - 전문분야: 법인세무, 부가가치세 신고
            - 경력: 10년
            - 추천 이유: 중소기업 세무 업무에 특화되어 있으며, 세무조사 대응 경험이 풍부합니다.
            - 예상 비용: 월 50-80만원
            - 매칭도: 94%
            
            **추천 전문가 2: 박노무 노무사**
            - 전문분야: 근로계약, 사회보험 업무
            - 경력: 7년
            - 추천 이유: 스타트업과 중소기업의 인사노무 컨설팅을 전문으로 하며, 실무적인 해결책 제시에 뛰어납니다.
            - 예상 비용: 건당 30-50만원
            - 매칭도: 88%
            
            귀하의 비즈니스 상황에 맞는 전문적인 법무 지원을 받으실 수 있습니다.
            """;
    }
    
    private String generateTranslationMockResponse(String input) {
        return """
            🌐 번역/통역 분야 추천 분석 결과
            
            언어 서비스 요구사항에 따른 전문가 추천:
            
            **추천 전문가 1: 김글로벌 번역가**
            - 전문분야: 영한/한영 비즈니스 번역
            - 경력: 8년
            - 추천 이유: 기술 문서와 계약서 번역에 특화되어 있으며, 정확성과 신속성을 겸비했습니다.
            - 예상 비용: 페이지당 2.5-4만원
            - 매칭도: 93%
            
            **추천 전문가 2: 박통역 통역사**
            - 전문분야: 비즈니스 회의 동시통역
            - 경력: 6년
            - 추천 이유: 국제회의 및 비즈니스 미팅 통역 경험이 풍부하며, 전문 용어 구사력이 뛰어납니다.
            - 예상 비용: 시간당 12-20만원
            - 매칭도: 90%
            
            언어적 정확성과 문화적 적절성을 모두 고려한 추천입니다.
            """;
    }
    
    private String generateGeneralMockResponse(String input) {
        return """
            🤖 AI 추천 분석 결과
            
            입력해주신 내용을 분석하여 다음과 같은 추천을 제공합니다:
            
            **분석 결과:**
            - 프로젝트 복잡도: 중간 수준
            - 예상 소요 기간: 2-4주
            - 권장 전문가 수: 1-2명
            
            **추천 사항:**
            보다 정확한 추천을 위해 다음 정보를 추가로 제공해주시면 좋겠습니다:
            - 구체적인 프로젝트 범위
            - 예산 범위
            - 완료 희망 일정
            - 선호하는 작업 방식 (온라인/오프라인)
            
            추가 정보를 바탕으로 더욱 정확한 전문가 매칭을 도와드리겠습니다.
            """;
    }
}
