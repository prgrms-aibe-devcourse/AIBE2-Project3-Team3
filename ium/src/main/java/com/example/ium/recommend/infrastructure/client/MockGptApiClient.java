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
        String expertName = expert.getMember().getUsername();
        String major = expert.getMajor() != null ? expert.getMajor() : "다양한 분야";
        int careerYears = java.time.Period.between(expert.getCareerDate().getStartDate(), java.time.LocalDate.now()).getYears();
        
        // 더 다양한 추천 이유 템플릿
        String[] generalTemplates = {
            "%s님은 %s 전공으로 %d년간의 풍부한 경험을 바탕으로 고품질의 결과물을 제공합니다.",
            "%d년 경력의 %s님은 창의적이고 전문적인 접근으로 프로젝트를 성공적으로 완수할 수 있습니다.",
            "%s 분야 전문가 %s님은 %d년간 축적한 노하우로 최적의 솔루션을 제안드릴 수 있습니다.",
            "전문성과 경험을 겸비한 %s님(%s, %d년 경력)이 귀하의 프로젝트에 완벽한 파트너가 될 것입니다."
        };
        
        String baseRecommendation = String.format(
            generalTemplates[random.nextInt(generalTemplates.length)],
            expertName, major, careerYears
        );
        
        // 사용자 요청에 따른 구체적인 추가 설명
        String specificAddition = generateSpecificRecommendation(lowercaseRequest, expert, careerYears);
        
        return baseRecommendation + " " + specificAddition;
    }
    
    /**
     * 사용자 요청별 구체적인 추천 이유 생성
     */
    private String generateSpecificRecommendation(String lowercaseRequest, ExpertProfile expert, int careerYears) {
        // NFT/디지털 아트 관련
        if (lowercaseRequest.contains("nft") || lowercaseRequest.contains("디지털") || 
            (lowercaseRequest.contains("아트") && !lowercaseRequest.contains("스마트"))) {
            String[] nftTemplates = {
                "블록체인 기반 NFT 프로젝트 경험으로 트렌디한 디지털 아트워크를 완성해드립니다.",
                "메타버스 시대에 맞는 혁신적인 NFT 컬렉션 제작이 가능합니다.",
                "크립토 아트 트렌드를 반영한 독창적인 NFT 디자인을 제공합니다."
            };
            return nftTemplates[random.nextInt(nftTemplates.length)];
        }
        
        // 웹사이트/UI 관련
        if (lowercaseRequest.contains("웹사이트") || lowercaseRequest.contains("ui") || 
            lowercaseRequest.contains("사이트") || lowercaseRequest.contains("인터페이스")) {
            String[] webTemplates = {
                "사용자 중심의 직관적인 UI/UX 설계로 웹사이트의 전환율을 높여드립니다.",
                "반응형 디자인과 최신 웹 트렌드를 적용한 모던한 웹사이트를 제작합니다.",
                "브랜드 아이덴티티를 반영한 일관성 있는 웹 인터페이스 디자인이 가능합니다.",
                "모바일 퍼스트 접근법으로 모든 디바이스에서 완벽한 사용자 경험을 제공합니다."
            };
            return webTemplates[random.nextInt(webTemplates.length)];
        }
        
        // 로고/브랜딩 관련
        if (lowercaseRequest.contains("로고") || lowercaseRequest.contains("브랜딩") || 
            lowercaseRequest.contains("브랜드") || lowercaseRequest.contains("아이덴티티")) {
            String[] brandingTemplates = {
                "브랜드의 핵심 가치를 담은 강력하고 기억에 남는 로고를 제작해드립니다.",
                "시장에서 차별화되는 독창적인 브랜드 아이덴티티 구축이 가능합니다.",
                "타겟 고객에게 어필하는 감성적인 브랜드 스토리를 시각적으로 표현합니다.",
                "확장 가능한 브랜딩 시스템으로 일관된 브랜드 경험을 제공합니다."
            };
            return brandingTemplates[random.nextInt(brandingTemplates.length)];
        }
        
        // 패키지 디자인 관련
        if (lowercaseRequest.contains("패키지") || lowercaseRequest.contains("포장") || 
            lowercaseRequest.contains("제품")) {
            String[] packageTemplates = {
                "소비자의 구매 욕구를 자극하는 매력적인 패키지 디자인을 제작합니다.",
                "제품의 특성을 반영한 기능적이면서도 아름다운 패키지 솔루션을 제공합니다.",
                "브랜드 가치를 극대화하는 프리미엄 패키지 디자인이 가능합니다.",
                "친환경적이고 지속가능한 패키지 디자인으로 브랜드 이미지를 높입니다."
            };
            return packageTemplates[random.nextInt(packageTemplates.length)];
        }
        
        // 앱 관련
        if (lowercaseRequest.contains("앱") || lowercaseRequest.contains("어플") || 
            lowercaseRequest.contains("모바일")) {
            String[] appTemplates = {
                "직관적인 사용자 인터페이스로 앱의 사용성과 만족도를 극대화합니다.",
                "최신 디자인 트렌드를 반영한 세련된 모바일 앱 디자인을 제공합니다.",
                "사용자 행동 패턴을 분석한 UX 최적화로 앱 성과를 향상시킵니다.",
                "크로스 플랫폼 일관성을 고려한 통합 앱 디자인 솔루션을 제공합니다."
            };
            return appTemplates[random.nextInt(appTemplates.length)];
        }
        
        // 영상/편집 관련
        if (lowercaseRequest.contains("영상") || lowercaseRequest.contains("편집") || 
            lowercaseRequest.contains("비디오") || lowercaseRequest.contains("동영상")) {
            String[] videoTemplates = {
                "스토리텔링이 살아있는 감동적인 영상 콘텐츠를 제작해드립니다.",
                "최신 편집 기법과 창의적인 연출로 퀄리티 높은 영상을 완성합니다.",
                "브랜드 메시지를 효과적으로 전달하는 임팩트 있는 영상을 제작합니다.",
                "타겟 플랫폼에 최적화된 다양한 포맷의 영상 콘텐츠를 제공합니다."
            };
            return videoTemplates[random.nextInt(videoTemplates.length)];
        }
        
        // 프로그래밍/개발 관련
        if (lowercaseRequest.contains("개발") || lowercaseRequest.contains("프로그래밍") || 
            lowercaseRequest.contains("코딩") || lowercaseRequest.contains("시스템")) {
            String[] devTemplates = {
                "확장 가능하고 안정적인 코드 구조로 장기적으로 유지보수가 용이한 시스템을 구축합니다.",
                "최신 기술 스택을 활용한 현대적이고 효율적인 개발 솔루션을 제공합니다.",
                "성능 최적화와 보안을 고려한 견고한 애플리케이션 개발이 가능합니다.",
                "빠른 개발 사이클과 지속적인 통합으로 프로젝트 일정을 단축시킵니다."
            };
            return devTemplates[random.nextInt(devTemplates.length)];
        }
        
        // 번역/통역 관련
        if (lowercaseRequest.contains("번역") || lowercaseRequest.contains("통역") || 
            lowercaseRequest.contains("영어") || lowercaseRequest.contains("언어")) {
            String[] translationTemplates = {
                "문화적 맥락을 고려한 자연스럽고 정확한 번역 서비스를 제공합니다.",
                "전문 분야별 용어에 능통하여 기술적 정확성을 보장하는 번역이 가능합니다.",
                "원문의 뉘앙스와 감정을 그대로 전달하는 고품질 번역을 제공합니다.",
                "비즈니스 목적에 맞는 효과적인 커뮤니케이션이 가능한 번역 서비스입니다."
            };
            return translationTemplates[random.nextInt(translationTemplates.length)];
        }
        
        // 법무/세무 관련
        if (lowercaseRequest.contains("법무") || lowercaseRequest.contains("세무") || 
            lowercaseRequest.contains("계약") || lowercaseRequest.contains("법률")) {
            String[] legalTemplates = {
                "복잡한 법적 이슈를 명확하게 분석하여 실무적인 해결책을 제시합니다.",
                "풍부한 실무 경험을 바탕으로 리스크를 최소화하는 전문적인 자문을 제공합니다.",
                "최신 법령과 판례를 반영한 정확하고 신뢰할 수 있는 법무 서비스입니다.",
                "고객의 비즈니스 상황을 이해하고 맞춤형 법적 솔루션을 제공합니다."
            };
            return legalTemplates[random.nextInt(legalTemplates.length)];
        }
        
        // 기본 템플릿 (키워드가 매칭되지 않는 경우)
        String[] defaultTemplates = {
            "다양한 프로젝트 경험을 바탕으로 창의적이고 실용적인 솔루션을 제공할 수 있습니다.",
            "고객의 니즈를 정확히 파악하여 기대 이상의 결과물을 제작해드립니다.",
            "트렌드를 반영하면서도 독창적인 접근으로 차별화된 결과물을 만들어냅니다.",
            "체계적인 프로세스와 세심한 피드백으로 만족스러운 프로젝트 진행이 가능합니다.",
            "전문성과 책임감으로 프로젝트의 성공적인 완수를 보장합니다."
        };
        
        return defaultTemplates[random.nextInt(defaultTemplates.length)];
    }
}
