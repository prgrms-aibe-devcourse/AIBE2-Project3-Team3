package com.example.ium.recommend.domain.service;

import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.model.expert.ExpertProfile;
import com.example.ium.member.domain.repository.ExpertProfileJPARepository;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.recommend.application.dto.request.GptRequestDto;
import com.example.ium.recommend.application.dto.response.ExpertRecommendationDto;
import com.example.ium.recommend.application.dto.response.GptResponseDto;
import com.example.ium.recommend.application.dto.response.WorkRequestRecommendationDto;
import com.example.ium.recommend.infrastructure.client.GptApiClient;
import com.example.ium.recommend.infrastructure.client.GptApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GPT API를 활용한 추천 서비스
 * 실제 GPT API 연동 및 응답 파싱 담당
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class GptRecommendationService {
    
    private final GptApiClient gptApiClient;
    private final ExpertDataCollectionService expertDataCollectionService;
    private final MemberJPARepository memberJPARepository;
    private final ExpertProfileJPARepository expertProfileJPARepository;
    
    /**
     * AI 전문가 매칭 추천 (신규 메서드)
     * 
     * @param memberId 사용자 ID
     * @param category 카테고리
     * @param userMessage 사용자 메시지
     * @return 추천 결과 (메시지 + 전문가 정보)
     */
    public Map<String, Object> getAIExpertRecommendation(Long memberId, String category, String userMessage) {
        log.debug("AI 전문가 매칭 추천 시작 - memberId: {}, category: {}", memberId, category);
        
        try {
            // 1. 사용자 프로필 데이터 수집
            String userProfileData = expertDataCollectionService.collectUserProfileData(memberId);
            
            // 2. 해당 카테고리의 전문가들 프로필 데이터 수집
            String expertProfilesData = expertDataCollectionService.collectExpertProfilesData(category);
            
            // 3. GPT 프롬프트 생성
            String prompt = buildAIMatchingPrompt(userProfileData, expertProfilesData, userMessage, category);
            
            // 4. GPT API 호출
            GptRequestDto request = GptRequestDto.createRecommendationRequest(prompt);
            GptResponseDto response = gptApiClient.sendRecommendationRequest(request);
            
            if (!response.isSuccessful()) {
                log.warn("GPT API 응답 비정상 - category: {}", category);
                return createFallbackResponse(category);
            }
            
            // 5. GPT 응답에서 추천 전문가 정보 파싱
            Map<String, Object> parsedResult = parseGptRecommendationResponse(response.getOutputText(), category);
            
            if (parsedResult.isEmpty()) {
                log.warn("GPT 응답 파싱 실패 - category: {}", category);
                return createFallbackResponse(category);
            }
            
            log.debug("AI 전문가 매칭 추천 완료 - memberId: {}, category: {}", memberId, category);
            return parsedResult;
            
        } catch (GptApiException e) {
            log.error("AI 전문가 매칭 GPT API 호출 실패 - memberId: {}, category: {}, error: {}", memberId, category, e.getMessage());
            return createFallbackResponse(category);
        } catch (Exception e) {
            log.error("AI 전문가 매칭 중 예상치 못한 오류 - memberId: {}, category: {}", memberId, category, e);
            return createFallbackResponse(category);
        }
    }
    
    /**
     * 전문가 추천 요청
     * 
     * @param userProfileData 사용자 프로필 데이터
     * @param expertProfilesData 전문가들 프로필 데이터
     * @param userMessage 사용자 메시지
     * @param category 카테고리
     * @return 추천된 전문가 목록
     */
    public List<ExpertRecommendationDto> getExpertRecommendations(
            String userProfileData, 
            String expertProfilesData, 
            String userMessage, 
            String category) {
        
        log.debug("전문가 추천 GPT 요청 시작 - category: {}", category);
        
        try {
            // GPT 요청 메시지 생성
            String prompt = buildExpertRecommendationPrompt(userProfileData, expertProfilesData, userMessage, category);
            
            // GPT API 호출
            GptRequestDto request = GptRequestDto.createRecommendationRequest(prompt);
            GptResponseDto response = gptApiClient.sendRecommendationRequest(request);
            
            if (!response.isSuccessful()) {
                log.warn("GPT API 응답 비정상 - category: {}", category);
                return generateDummyExpertRecommendations(category);
            }
            
            // 실제 GPT 응답을 사용할 수 있지만, 현재는 구조화된 데이터를 위해 더미 데이터 사용
            // TODO: 5단계에서 GPT 응답을 구조화된 데이터로 파싱하는 로직 추가
            log.debug("GPT 응답 수신: {}", response.getOutputText().substring(0, Math.min(100, response.getOutputText().length())));
            
            return generateDummyExpertRecommendations(category);
            
        } catch (GptApiException e) {
            log.error("전문가 추천 GPT API 호출 실패 - category: {}, error: {}", category, e.getMessage());
            // API 실패 시 더미 데이터로 폴백
            return generateDummyExpertRecommendations(category);
        }
    }
    
    /**
     * 의뢰 추천 요청
     * 
     * @param expertProfileData 전문가 프로필 데이터
     * @param workRequestsData 의뢰들 데이터
     * @param userMessage 사용자 메시지
     * @param category 카테고리
     * @return 추천된 의뢰 목록
     */
    public List<WorkRequestRecommendationDto> getWorkRequestRecommendations(
            String expertProfileData, 
            String workRequestsData, 
            String userMessage, 
            String category) {
        
        log.debug("의뢰 추천 GPT 요청 시작 - category: {}", category);
        
        try {
            // GPT 요청 메시지 생성
            String prompt = buildWorkRequestRecommendationPrompt(expertProfileData, workRequestsData, userMessage, category);
            
            // GPT API 호출
            GptRequestDto request = GptRequestDto.createRecommendationRequest(prompt);
            GptResponseDto response = gptApiClient.sendRecommendationRequest(request);
            
            if (!response.isSuccessful()) {
                log.warn("GPT API 응답 비정상 - category: {}", category);
                return generateDummyWorkRequestRecommendations(category);
            }
            
            // 실제 GPT 응답을 사용할 수 있지만, 현재는 구조화된 데이터를 위해 더미 데이터 사용
            // TODO: 5단계에서 GPT 응답을 구조화된 데이터로 파싱하는 로직 추가
            log.debug("GPT 응답 수신: {}", response.getOutputText().substring(0, Math.min(100, response.getOutputText().length())));
            
            return generateDummyWorkRequestRecommendations(category);
            
        } catch (GptApiException e) {
            log.error("의뢰 추천 GPT API 호출 실패 - category: {}, error: {}", category, e.getMessage());
            // API 실패 시 더미 데이터로 폴백
            return generateDummyWorkRequestRecommendations(category);
        }
    }
    
    /**
     * 전문가 추천 메시지 생성
     */
    public String generateExpertRecommendationMessage(
            List<ExpertRecommendationDto> experts, 
            String category, 
            String userMessage) {
        
        if (experts.isEmpty()) {
            return "죄송합니다. 현재 " + getCategoryName(category) + " 분야에 적합한 전문가를 찾을 수 없습니다.";
        }
        
        StringBuilder message = new StringBuilder();
        message.append("💡 ").append(getCategoryName(category)).append(" 분야의 추천 전문가들을 찾았습니다!\n\n");
        
        for (int i = 0; i < experts.size(); i++) {
            ExpertRecommendationDto expert = experts.get(i);
            message.append("**").append(i + 1).append(". ").append(expert.getExpertName()).append("**\n");
            message.append("✨ ").append(expert.getAiRecommendReason()).append("\n");
            message.append("💰 예상 비용: ").append(expert.getSalary()).append("만원\n");
            message.append("⭐ 매칭도: ").append(String.format("%.1f", expert.getMatchingScore() * 100)).append("%\n\n");
        }
        
        message.append("원하시는 전문가와 채팅을 통해 자세한 상담을 받아보세요! 🚀");
        
        return message.toString();
    }
    
    /**
     * 의뢰 추천 메시지 생성
     */
    public String generateWorkRequestRecommendationMessage(
            List<WorkRequestRecommendationDto> workRequests, 
            String category, 
            String userMessage) {
        
        if (workRequests.isEmpty()) {
            return "죄송합니다. 현재 " + getCategoryName(category) + " 분야에 적합한 의뢰를 찾을 수 없습니다.";
        }
        
        StringBuilder message = new StringBuilder();
        message.append("🎯 ").append(getCategoryName(category)).append(" 분야의 추천 의뢰들을 찾았습니다!\n\n");
        
        for (int i = 0; i < workRequests.size(); i++) {
            WorkRequestRecommendationDto request = workRequests.get(i);
            message.append("**").append(i + 1).append(". ").append(request.getTitle()).append("**\n");
            message.append("✨ ").append(request.getAiRecommendReason()).append("\n");
            message.append("💰 예상 수익: ").append(request.getBudget()).append("원\n");
            message.append("⭐ 매칭도: ").append(String.format("%.1f", request.getMatchingScore() * 100)).append("%\n\n");
        }
        
        message.append("관심있는 의뢰에 지원해보세요! 💪");
        
        return message.toString();
    }
    
    /**
     * 전문가 추천용 GPT 프롬프트 생성
     */
    private String buildExpertRecommendationPrompt(String userProfileData, String expertProfilesData, String userMessage, String category) {
        return String.format("""
            당신은 전문가와 클라이언트를 매칭해주는 AI 어시스턴트입니다.
            
            **사용자 정보:**
            %s
            
            **사용자 요청:**
            %s
            
            **사용 가능한 전문가들:**
            %s
            
            **요청사항:**
            위 정보를 바탕으로 %s 분야에서 가장 적합한 전문가 2-3명을 추천하고, 각 전문가에 대한 추천 이유를 상세히 설명해주세요.
            추천도는 0-100%% 범위로 표현해주세요.
            """, userProfileData, userMessage, expertProfilesData, getCategoryName(category));
    }
    
    /**
     * 의뢰 추천용 GPT 프롬프트 생성
     */
    private String buildWorkRequestRecommendationPrompt(String expertProfileData, String workRequestsData, String userMessage, String category) {
        return String.format("""
            당신은 전문가에게 적합한 의뢰를 추천해주는 AI 어시스턴트입니다.
            
            **전문가 정보:**
            %s
            
            **전문가 요청:**
            %s
            
            **사용 가능한 의뢰들:**
            %s
            
            **요청사항:**
            위 정보를 바탕으로 %s 분야에서 이 전문가에게 가장 적합한 의뢰 2-3개를 추천하고, 각 의뢰에 대한 추천 이유를 상세히 설명해주세요.
            추천도는 0-100%% 범위로 표현해주세요.
            """, expertProfileData, userMessage, workRequestsData, getCategoryName(category));
    }
    
    /**
     * 임시 전문가 추천 더미 데이터 생성
     */
    private List<ExpertRecommendationDto> generateDummyExpertRecommendations(String category) {
        return switch (category) {
            case "design" -> List.of(
                ExpertRecommendationDto.builder()
                    .expertId(1L)
                    .expertName("김디자인")
                    .introduceMessage("사용자 경험을 중시하는 디자인 전문가입니다")
                    .specializations(List.of("UI/UX 디자인"))
                    .school("디자인대학교")
                    .major("시각디자인")
                    .salary(75)
                    .negoYn(true)
                    .completedRequestCount(15)
                    .profileImageUrl(null)
                    .aiRecommendReason("사용자 경험을 중시하는 디자인 전문가입니다")
                    .matchingScore(0.95)
                    .build(),
                ExpertRecommendationDto.builder()
                    .expertId(2L)
                    .expertName("박그래픽")
                    .introduceMessage("브랜딩과 로고 디자인 분야의 베테랑입니다")
                    .specializations(List.of("그래픽 디자인"))
                    .school("예술대학교")
                    .major("그래픽디자인")
                    .salary(60)
                    .negoYn(true)
                    .completedRequestCount(25)
                    .profileImageUrl(null)
                    .aiRecommendReason("브랜딩과 로고 디자인 분야의 베테랑입니다")
                    .matchingScore(0.88)
                    .build()
            );
            case "programming" -> List.of(
                ExpertRecommendationDto.builder()
                    .expertId(3L)
                    .expertName("이개발")
                    .introduceMessage("반응형 웹사이트 개발 전문가입니다")
                    .specializations(List.of("웹 개발"))
                    .school("공과대학교")
                    .major("컴퓨터공학")
                    .salary(150)
                    .negoYn(false)
                    .completedRequestCount(30)
                    .profileImageUrl(null)
                    .aiRecommendReason("반응형 웹사이트 개발 전문가입니다")
                    .matchingScore(0.92)
                    .build()
            );
            default -> List.of();
        };
    }
    
    /**
     * 임시 의뢰 추천 더미 데이터 생성
     */
    private List<WorkRequestRecommendationDto> generateDummyWorkRequestRecommendations(String category) {
        return switch (category) {
            case "design" -> List.of(
                WorkRequestRecommendationDto.builder()
                    .workRequestId(1L)
                    .title("스타트업 로고 디자인 의뢰")
                    .description("스타트업 브랜드 로고 디자인")
                    .category("디자인")
                    .clientName("김클라이언트")
                    .budget(500000)
                    .deadline(null)
                    .createdAt(null)
                    .status("OPEN")
                    .requiredSkills(List.of("로고디자인", "브랜딩"))
                    .location("온라인")
                    .workType("FORMAL")
                    .aiRecommendReason("당신의 브랜딩 전문성에 적합한 프로젝트입니다")
                    .matchingScore(0.90)
                    .build()
            );
            case "programming" -> List.of(
                WorkRequestRecommendationDto.builder()
                    .workRequestId(2L)
                    .title("쇼핑몰 웹사이트 개발")
                    .description("전자상거래 웹사이트 개발")
                    .category("프로그래밍")
                    .clientName("박클라이언트")
                    .budget(1500000)
                    .deadline(null)
                    .createdAt(null)
                    .status("OPEN")
                    .requiredSkills(List.of("웹개발", "프론트엔드", "백엔드"))
                    .location("온라인")
                    .workType("FORMAL")
                    .aiRecommendReason("당신의 웹 개발 경험에 딱 맞는 프로젝트입니다")
                    .matchingScore(0.88)
                    .build()
            );
            default -> List.of();
        };
    }
    
    /**
     * AI 매칭용 GPT 프롬프트 생성
     */
    private String buildAIMatchingPrompt(String userProfileData, String expertProfilesData, String userMessage, String category) {
        return String.format("""
            당신은 전문가와 클라이언트를 매칭해주는 AI 어시스턴트입니다.
            
            **사용자 정보:**
            %s
            
            **사용자 요청:**
            %s
            
            **사용 가능한 전문가들:**
            %s
            
            **요청사항:**
            위 정보를 바탕으로 %s 분야에서 가장 적합한 전문가 1명을 추천해주세요.
            
            **응답 형식:** 다음 형식으로 정확히 응답해주세요.
            EXPERT_ID: [전문가ID]
            EXPERT_NAME: [전문가이름]
            EXPERT_EMAIL: [전문가이메일]
            RECOMMENDATION: [추천이유 및 전문가 설명 (고객에게 말하는 방식으로 2-3문장)]
            """, userProfileData, userMessage, expertProfilesData, getCategoryName(category));
    }
    
    /**
     * GPT 응답에서 추천 전문가 정보 파싱
     */
    private Map<String, Object> parseGptRecommendationResponse(String gptResponse, String category) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 정규식으로 전문가 정보 추출
            Pattern expertIdPattern = Pattern.compile("EXPERT_ID:\\s*([0-9]+)");
            Pattern expertNamePattern = Pattern.compile("EXPERT_NAME:\\s*(.+)");
            Pattern expertEmailPattern = Pattern.compile("EXPERT_EMAIL:\\s*(.+)");
            Pattern recommendationPattern = Pattern.compile("RECOMMENDATION:\\s*(.+?)(?=\\n\\n|$)", Pattern.DOTALL);
            
            Matcher idMatcher = expertIdPattern.matcher(gptResponse);
            Matcher nameMatcher = expertNamePattern.matcher(gptResponse);
            Matcher emailMatcher = expertEmailPattern.matcher(gptResponse);
            Matcher recommendationMatcher = recommendationPattern.matcher(gptResponse);
            
            if (idMatcher.find() && nameMatcher.find() && emailMatcher.find() && recommendationMatcher.find()) {
                Long expertId = Long.parseLong(idMatcher.group(1).trim());
                String expertName = nameMatcher.group(1).trim();
                String expertEmail = emailMatcher.group(1).trim();
                String recommendation = recommendationMatcher.group(1).trim();
                
                // 전문가 정보 검증 및 실제 데이터 가져오기
                ExpertProfile expert = expertProfileJPARepository.findByIdByEagerLoading(expertId).orElse(null);
                if (expert != null && expert.isActivated()) {
                    // 실제 전문가 이메일 가져오기
                    String realExpertEmail = expert.getMember().getEmail().getValue();
                    String realExpertName = expert.getMember().getUsername();
                    
                    Map<String, Object> expertInfo = new HashMap<>();
                    expertInfo.put("id", expertId);
                    expertInfo.put("name", realExpertName);
                    expertInfo.put("email", realExpertEmail);
                    expertInfo.put("school", expert.getSchool());
                    expertInfo.put("major", expert.getMajor());
                    expertInfo.put("salary", expert.getSalary().getValue());
                    expertInfo.put("negoYn", expert.getNegoYn().isNegotiable());
                    expertInfo.put("introduceMessage", expert.getIntroduceMessage());
                    
                    result.put("message", String.format("🎆 %s 분야에 딱 맞는 전문가를 찾았어요!\n\n👨‍💼 **%s**\n%s\n\n💬 지금 바로 연락해보세요: %s", 
                            getCategoryName(category), realExpertName, recommendation, realExpertEmail));
                    result.put("expertInfo", expertInfo);
                    
                    log.debug("GPT 응답 파싱 성공 - expertId: {}, expertName: {}", expertId, realExpertName);
                    return result;
                }
            }
            
            log.warn("GPT 응답 파싱 실패 - 형식이 맞지 않거나 전문가를 찾을 수 없음: {}", gptResponse.substring(0, Math.min(200, gptResponse.length())));
            
        } catch (Exception e) {
            log.error("GPT 응답 파싱 중 오류 발생", e);
        }
        
        return result;
    }
    
    /**
     * 폴백 응답 생성 (GPT 실패 시)
     */
    private Map<String, Object> createFallbackResponse(String category) {
        Map<String, Object> response = new HashMap<>();
        
        String fallbackMessage = String.format(
            "죄송합니다. 현재 %s 분야에서 가장 적합한 전문가를 찾는 중입니다. \n\n" +
            "잠시 후 다시 시도하거나, 더 구체적인 요구사항을 말씀해주시면 \n" +
            "더 정확한 추천을 드릴 수 있습니다. 🚀",
            getCategoryName(category)
        );
        
        response.put("message", fallbackMessage);
        response.put("expertInfo", null);
        
        return response;
    }
    
    /**
     * 카테고리 코드를 한글명으로 변환
     */
    private String getCategoryName(String category) {
        return switch (category) {
            case "design" -> "디자인";
            case "programming" -> "프로그래밍";
            case "video" -> "영상편집";
            case "legal" -> "세무/법무/노무";
            case "translation" -> "번역/통역";
            default -> "기타";
        };
    }
}
