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
     * 전문가의 자기소개(introduceMessage)를 분석하여 자연스럽고 개인화된 추천 이유 작성
     */
    private String generateRecommendationText(String userRequest, ExpertProfile expert) {
        String expertName = expert.getMember().getUsername();
        String major = expert.getMajor() != null ? expert.getMajor() : "다양한 분야";
        int careerYears = java.time.Period.between(expert.getCareerDate().getStartDate(), java.time.LocalDate.now()).getYears();
        String introduceMessage = expert.getIntroduceMessage() != null ? expert.getIntroduceMessage() : "다양한 프로젝트 경험";
        String school = expert.getSchool() != null ? expert.getSchool() : "";
        
        // 경력 및 학력 정보 정리
        StringBuilder backgroundInfo = new StringBuilder();
        backgroundInfo.append(String.format("%s 전공", major));
        if (!school.isEmpty()) {
            backgroundInfo.append(String.format(" (%s 출신)", school));
        }
        backgroundInfo.append(String.format(", %d년 경력", careerYears));
        
        // 자기소개 기반 자연스럽고 개인화된 추천 이유 생성
        String recommendation = generateIntroduceBasedRecommendation(userRequest, introduceMessage, expertName);
        
        // 최종 형태: "[추천이유] [경력정보]"
        return String.format("%s \n\n📋 전문가 배경: %s", recommendation, backgroundInfo.toString());
    }
    
    /**
     * 자기소개 메시지를 분석하여 자연스럽고 개인화된 추천 이유 생성
     */
    private String generateIntroduceBasedRecommendation(String userRequest, String introduceMessage, String expertName) {
        String lowercaseRequest = userRequest.toLowerCase();
        String lowercaseIntroduce = introduceMessage.toLowerCase();
        
        // 자기소개에서 키워드 추출 및 분석
        StringBuilder recommendation = new StringBuilder();
        
        // 자기소개 기반 강점 분석
        if (lowercaseIntroduce.contains("react") || lowercaseIntroduce.contains("vue") || lowercaseIntroduce.contains("angular")) {
            recommendation.append(String.format("%s님은 최신 프론트엔드 기술에 능숙하며, ", expertName));
        } else if (lowercaseIntroduce.contains("spring") || lowercaseIntroduce.contains("node") || lowercaseIntroduce.contains("백엔드")) {
            recommendation.append(String.format("%s님은 안정적인 서버 개발 전문가로, ", expertName));
        } else if (lowercaseIntroduce.contains("디자인") || lowercaseIntroduce.contains("브랜딩") || lowercaseIntroduce.contains("ui")) {
            recommendation.append(String.format("%s님은 창의적인 디자인 감각을 바탕으로, ", expertName));
        } else if (lowercaseIntroduce.contains("영상") || lowercaseIntroduce.contains("편집") || lowercaseIntroduce.contains("모션")) {
            recommendation.append(String.format("%s님은 뛰어난 영상 제작 역량으로, ", expertName));
        } else if (lowercaseIntroduce.contains("번역") || lowercaseIntroduce.contains("통역") || lowercaseIntroduce.contains("영어")) {
            recommendation.append(String.format("%s님은 전문적인 언어 능력을 활용하여, ", expertName));
        } else if (lowercaseIntroduce.contains("세무") || lowercaseIntroduce.contains("법무") || lowercaseIntroduce.contains("회계")) {
            recommendation.append(String.format("%s님은 풍부한 법무/세무 경험을 바탕으로, ", expertName));
        } else {
            recommendation.append(String.format("%s님의 전문성과 열정을 바탕으로, ", expertName));
        }
        
        // 자기소개에서 구체적인 성과나 경험 추출
        if (lowercaseIntroduce.contains("대기업") || lowercaseIntroduce.contains("삼성") || lowercaseIntroduce.contains("lg") || 
            lowercaseIntroduce.contains("네이버") || lowercaseIntroduce.contains("카카오")) {
            recommendation.append("대기업 프로젝트 경험을 통해 검증된 실력으로 ");
        } else if (lowercaseIntroduce.contains("스타트업") || lowercaseIntroduce.contains("startup")) {
            recommendation.append("스타트업 환경에서 쌓은 빠른 적응력과 문제해결 능력으로 ");
        } else if (lowercaseIntroduce.contains("100만") || lowercaseIntroduce.contains("1000만") || lowercaseIntroduce.contains("조회수")) {
            recommendation.append("높은 성과를 달성한 검증된 역량으로 ");
        } else if (lowercaseIntroduce.contains("1위") || lowercaseIntroduce.contains("top") || lowercaseIntroduce.contains("수상")) {
            recommendation.append("업계에서 인정받은 뛰어난 실력으로 ");
        } else if (lowercaseIntroduce.contains("15건") || lowercaseIntroduce.contains("50개") || lowercaseIntroduce.contains("100편") || 
                   lowercaseIntroduce.contains("200건") || lowercaseIntroduce.contains("500편")) {
            recommendation.append("다수의 프로젝트를 성공적으로 완수한 풍부한 경험으로 ");
        } else if (lowercaseIntroduce.contains("열정") || lowercaseIntroduce.contains("매력") || lowercaseIntroduce.contains("성실")) {
            recommendation.append("전문성과 열정을 바탕으로 ");
        } else {
            recommendation.append("축적된 전문 지식과 실무 경험을 바탕으로 ");
        }
        
        // 사용자 요청과 매칭되는 구체적인 추천 이유
        String specificMatch = generateUserRequestMatch(lowercaseRequest, lowercaseIntroduce);
        recommendation.append(specificMatch);
        
        return recommendation.toString();
    }
    
    /**
     * 사용자 요청과 자기소개 매칭도를 분석하여 구체적인 추천 이유 생성
     */
    private String generateUserRequestMatch(String lowercaseRequest, String lowercaseIntroduce) {
        // NFT/디지털 아트 요청
        if (lowercaseRequest.contains("nft") || (lowercaseRequest.contains("디지털") && lowercaseRequest.contains("아트"))) {
            if (lowercaseIntroduce.contains("블록체인") || lowercaseIntroduce.contains("nft") || lowercaseIntroduce.contains("크립토")) {
                return "블록체인 기반 NFT 프로젝트 경험을 살려 트렌디하고 가치 있는 디지털 아트 컬렉션을 완성해드릴 수 있습니다.";
            } else if (lowercaseIntroduce.contains("디지털") || lowercaseIntroduce.contains("그래픽")) {
                return "디지털 아트 제작 역량을 바탕으로 독창적이고 매력적인 NFT 아트워크를 제작해드릴 수 있습니다.";
            } else {
                return "창의적인 디자인 감각으로 시장에서 주목받을 수 있는 NFT 아트 컬렉션을 기획하고 제작해드리겠습니다.";
            }
        }
        
        // 웹사이트/UI 요청
        if (lowercaseRequest.contains("웹사이트") || lowercaseRequest.contains("ui") || lowercaseRequest.contains("인터페이스")) {
            if (lowercaseIntroduce.contains("반응형") || lowercaseIntroduce.contains("responsive")) {
                return "반응형 웹 디자인 전문성으로 모든 디바이스에서 완벽한 사용자 경험을 제공하는 웹사이트를 구축해드리겠습니다.";
            } else if (lowercaseIntroduce.contains("ux") || lowercaseIntroduce.contains("사용자")) {
                return "사용자 중심의 UX 설계 경험을 통해 직관적이고 효과적인 웹 인터페이스를 디자인해드릴 수 있습니다.";
            } else if (lowercaseIntroduce.contains("전환율") || lowercaseIntroduce.contains("conversion")) {
                return "전환율 최적화 노하우를 활용하여 비즈니스 목표 달성에 기여하는 고성능 웹사이트를 제작해드리겠습니다.";
            } else {
                return "웹 개발 전문성을 바탕으로 브랜드 가치를 높이는 세련되고 기능적인 웹사이트를 완성해드릴 수 있습니다.";
            }
        }
        
        // 브랜딩/로고 요청
        if (lowercaseRequest.contains("브랜딩") || lowercaseRequest.contains("로고") || lowercaseRequest.contains("브랜드")) {
            if (lowercaseIntroduce.contains("아이덴티티") || lowercaseIntroduce.contains("identity")) {
                return "브랜드 아이덴티티 구축 전문성으로 시장에서 차별화되고 기억에 남는 브랜드를 만들어드리겠습니다.";
            } else if (lowercaseIntroduce.contains("리브랜딩") || lowercaseIntroduce.contains("rebranding")) {
                return "리브랜딩 프로젝트 경험을 통해 브랜드의 새로운 가치를 발굴하고 시각적으로 구현해드릴 수 있습니다.";
            } else {
                return "브랜드의 핵심 가치와 개성을 담아 타겟 고객에게 강력하게 어필하는 브랜딩 솔루션을 제공해드리겠습니다.";
            }
        }
        
        // 영상/편집 요청
        if (lowercaseRequest.contains("영상") || lowercaseRequest.contains("편집") || lowercaseRequest.contains("비디오")) {
            if (lowercaseIntroduce.contains("유튜브") || lowercaseIntroduce.contains("youtube")) {
                return "유튜브 콘텐츠 제작 경험을 바탕으로 시청자의 관심을 끌고 채널 성장에 기여하는 영상을 제작해드리겠습니다.";
            } else if (lowercaseIntroduce.contains("홍보") || lowercaseIntroduce.contains("광고")) {
                return "홍보영상 제작 노하우를 활용하여 브랜드 메시지를 효과적으로 전달하는 임팩트 있는 영상을 완성해드릴 수 있습니다.";
            } else if (lowercaseIntroduce.contains("모션그래픽") || lowercaseIntroduce.contains("애니메이션")) {
                return "모션그래픽과 애니메이션 기술로 시각적 몰입도가 높은 창의적인 영상 콘텐츠를 제작해드리겠습니다.";
            } else {
                return "영상 제작 전문성으로 스토리텔링이 살아있는 감동적이고 기억에 남는 영상을 완성해드릴 수 있습니다.";
            }
        }
        
        // 앱 개발 요청
        if (lowercaseRequest.contains("앱") || lowercaseRequest.contains("어플") || lowercaseRequest.contains("모바일")) {
            if (lowercaseIntroduce.contains("네이티브") || lowercaseIntroduce.contains("ios") || lowercaseIntroduce.contains("android")) {
                return "네이티브 앱 개발 전문성으로 각 플랫폼에 최적화된 고성능 모바일 애플리케이션을 구축해드리겠습니다.";
            } else if (lowercaseIntroduce.contains("크로스플랫폼") || lowercaseIntroduce.contains("flutter") || lowercaseIntroduce.contains("react native")) {
                return "크로스플랫폼 개발 경험을 통해 효율적이고 일관된 사용자 경험을 제공하는 앱을 개발해드릴 수 있습니다.";
            } else {
                return "모바일 앱 개발 역량으로 사용자 친화적이고 비즈니스 목표에 부합하는 성공적인 앱을 제작해드리겠습니다.";
            }
        }
        
        // 번역/통역 요청
        if (lowercaseRequest.contains("번역") || lowercaseRequest.contains("통역") || lowercaseRequest.contains("영어")) {
            if (lowercaseIntroduce.contains("의학") || lowercaseIntroduce.contains("의료") || lowercaseIntroduce.contains("논문")) {
                return "의학/기술 전문 번역 경험으로 정확성과 전문성이 요구되는 문서를 완벽하게 번역해드릴 수 있습니다.";
            } else if (lowercaseIntroduce.contains("비즈니스") || lowercaseIntroduce.contains("계약서")) {
                return "비즈니스 번역 전문성으로 국제적인 커뮤니케이션과 계약 진행을 원활하게 지원해드리겠습니다.";
            } else if (lowercaseIntroduce.contains("동시통역") || lowercaseIntroduce.contains("회의")) {
                return "동시통역 경험을 바탕으로 중요한 회의와 행사에서 정확하고 자연스러운 통역 서비스를 제공해드릴 수 있습니다.";
            } else {
                return "언어 전문성과 문화적 이해를 바탕으로 원문의 의미와 뉘앙스를 정확히 전달하는 고품질 번역을 제공해드리겠습니다.";
            }
        }
        
        // 법무/세무 요청
        if (lowercaseRequest.contains("법무") || lowercaseRequest.contains("세무") || lowercaseRequest.contains("계약")) {
            if (lowercaseIntroduce.contains("상장회사") || lowercaseIntroduce.contains("대기업")) {
                return "대기업 법무/세무 업무 경험으로 복잡한 기업 이슈에 대해 전문적이고 실무적인 해결책을 제시해드릴 수 있습니다.";
            } else if (lowercaseIntroduce.contains("스타트업") || lowercaseIntroduce.contains("중소기업")) {
                return "스타트업과 중소기업 특화 법무/세무 서비스로 비즈니스 성장을 법적으로 안전하게 뒷받침해드리겠습니다.";
            } else if (lowercaseIntroduce.contains("세무조사") || lowercaseIntroduce.contains("분쟁")) {
                return "세무조사 대응과 법무 분쟁 해결 경험으로 리스크를 최소화하고 안정적인 사업 운영을 지원해드릴 수 있습니다.";
            } else {
                return "법무/세무 전문성을 바탕으로 정확한 법적 검토와 세무 처리로 안전한 비즈니스 환경을 구축해드리겠습니다.";
            }
        }
        
        // 기본 추천 (매칭되는 키워드가 없는 경우)
        return "전문적인 역량과 풍부한 경험을 바탕으로 고객의 요구사항을 정확히 이해하고 최고 품질의 결과물을 제공해드릴 수 있습니다.";
    }
}
