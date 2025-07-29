package com.example.ium.recommend.domain.service;

import com.example.ium.member.domain.model.expert.ExpertProfile;
import com.example.ium.member.domain.repository.ExpertProfileJPARepository;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.recommend.application.dto.request.GptRequestDto;
import com.example.ium.recommend.application.dto.response.GptResponseDto;
import com.example.ium.recommend.infrastructure.client.GptApiClient;
import com.example.ium.recommend.infrastructure.client.GptApiException;
import com.example.ium.specialization.domain.repository.SpecializationJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * GPT API를 활용한 AI 전문가 추천 서비스
 * 실제 GPT API 연동 및 응답 파싱 담당
 * 
 * ✅ 정리됨: 불필요한 Strategy 패턴 코드 제거 (2025.07.29)
 * ✅ 현재 사용: RecommendController에서 getAIExpertRecommendation() 메서드만 사용
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class GptRecommendationService {
    
    private final GptApiClient gptApiClient;
    private final ExpertDataCollectionService expertDataCollectionService;
    private final MemberJPARepository memberJPARepository;
    private final ExpertProfileJPARepository expertProfileJPARepository;
    private final SpecializationJPARepository specializationJPARepository;
    
    /**
     * AI 전문가 매칭 추천 (메인 메서드)
     * 
     * @param memberId 사용자 ID
     * @param category 카테고리 (legal, design, programming, video, translation)
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
     * AI 매칭용 GPT 프롬프트 생성
     */
    private String buildAIMatchingPrompt(String userProfileData, String expertProfilesData, String userMessage, String category) {
        String categoryDisplayName = getCategoryName(category);
        String targetSpecializationName = mapCategoryToSpecialization(category);
        
        // 디버깅용 로깅
        log.info("📝 AI 매칭 프롬프트 생성 - category: {} -> {}", category, targetSpecializationName);
        
        return String.format("""
            당신은 전문가와 클라이언트를 매칭해주는 AI 어시스턴트입니다.
            
            ⚠️ **절대 중요**: 아래 전문가들은 모두 %s 분야 전문가입니다. 다른 분야의 전문성을 언급하지 마세요.
            ⚠️ **필수**: 반드시 아래 전문가 목록에서만 선택하세요.
            
            **사용자 정보:**
            %s
            
            **사용자 요청:**
            %s
            
            **사용 가능한 전문가들:**
            %s
            
            **요청사항:**
            위 정보를 바탕으로 %s 분야에서 가장 적합한 전문가 1명을 추천해주세요.
            
            **⚠️ 중요한 제약사항:**
            1. 반드시 %s 분야 관련 전문성만 언급하세요
            2. 웹 개발, 앱 개발, 소프트웨어 개발 등 다른 분야 용어 사용 금지
            3. 전문가의 실제 자기소개와 전문분야에만 기반하여 작성
            4. 해당 전문가가 %s 분야가 아닌 경우 추천하지 마세요
            
            **응답 형식:** 다음 형식으로 정확히 응답해주세요.
            EXPERT_ID: [전문가ID]
            EXPERT_NAME: [전문가이름]
            EXPERT_EMAIL: [전문가이메일]
            RECOMMENDATION: [이 전문가의 %s 분야 전문성을 바탕으로 한 추천 이유를 2-3문장으로 설명. 절대로 다른 분야 전문성 언급 금지]
            """, 
            categoryDisplayName, userProfileData, userMessage, expertProfilesData, 
            categoryDisplayName, categoryDisplayName, categoryDisplayName, categoryDisplayName);
    }
    
    /**
     * GPT 응답에서 추천 전문가 정보 파싱
     */
    private Map<String, Object> parseGptRecommendationResponse(String gptResponse, String category) {
        Map<String, Object> result = new HashMap<>();
        
        // 추가 디버깅: GPT 응답 로깅
        log.info("🔍 GPT 응답 파싱 시작 - category: {}", category);
        log.debug("GPT 전체 응답: {}", gptResponse);
        
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
            
            log.debug("정규식 매칭 결과 - ID: {}, Name: {}, Email: {}, Recommendation: {}", 
                    idMatcher.find(), nameMatcher.find(), emailMatcher.find(), recommendationMatcher.find());
            
            // 매처 재설정
            idMatcher.reset();
            nameMatcher.reset();
            emailMatcher.reset();
            recommendationMatcher.reset();
            
            if (idMatcher.find() && nameMatcher.find() && emailMatcher.find() && recommendationMatcher.find()) {
                Long expertId = Long.parseLong(idMatcher.group(1).trim());
                String expertName = nameMatcher.group(1).trim();
                String expertEmail = emailMatcher.group(1).trim();
                String recommendation = recommendationMatcher.group(1).trim();
                
                log.info("🎯 GPT가 추천한 전문가 - ID: {}, Name: {}, Email: {}", expertId, expertName, expertEmail);
                log.debug("추천 사유: {}", recommendation);
                
                // 전문가 정보 검증 및 실제 데이터 가져오기
                ExpertProfile expert = expertProfileJPARepository.findByIdByEagerLoading(expertId).orElse(null);
                if (expert != null && expert.isActivated()) {
                    // 실제 전문가 이메일 가져오기
                    String realExpertEmail = expert.getMember().getEmail().getValue();
                    String realExpertName = expert.getMember().getUsername();
                    
                    // 전문가의 실제 전문분야 확인
                    String expertSpecializations = getExpertSpecializationNames(expert).stream()
                            .collect(Collectors.joining(", "));
                    String targetSpecializationName = mapCategoryToSpecialization(category);
                    
                    log.info("⚙️ 전문가 검증 - 요청 분야: {}, 전문가 분야: {}", targetSpecializationName, expertSpecializations);
                    
                    // 전문분야 매칭 검증
                    boolean isMatchingSpecialization = expertSpecializations.contains(targetSpecializationName);
                    
                    if (!isMatchingSpecialization) {
                        log.error("❌ 전문가 분야 불일치! 요청: {}, 전문가: {} (ID: {})", 
                                targetSpecializationName, expertSpecializations, expertId);
                        log.error("이는 데이터 필터링 또는 GPT 작동 문제일 수 있습니다.");
                        return result; // 빈 결과 반환
                    }
                    
                    Map<String, Object> expertInfo = new HashMap<>();
                    expertInfo.put("id", expertId);
                    expertInfo.put("name", realExpertName);
                    expertInfo.put("email", realExpertEmail);
                    expertInfo.put("profileUrl", "/expert-profiles/" + expertId);
                    expertInfo.put("school", expert.getSchool());
                    expertInfo.put("major", expert.getMajor());
                    expertInfo.put("salary", expert.getSalary().getValue());
                    expertInfo.put("negoYn", expert.getNegoYn().isNegotiable());
                    expertInfo.put("introduceMessage", expert.getIntroduceMessage());
                    expertInfo.put("completedRequestCount", expert.getCompletedRequestCount().getValue());
                    expertInfo.put("recommendation", recommendation);
                    
                    result.put("message", String.format("🎆 %s 분야에 딱 맞는 전문가를 찾았어요!\n\n%s", 
                            getCategoryName(category), recommendation));
                    result.put("expertInfo", expertInfo);
                    
                    log.info("✅ GPT 응답 파싱 성공 - expertId: {}, expertName: {}", expertId, realExpertName);
                    return result;
                } else {
                    log.error("❌ 전문가를 찾을 수 없거나 비활성화됨 - expertId: {}", expertId);
                }
            } else {
                log.error("❌ GPT 응답 형식 오류 - 필수 필드가 누락됨");
                log.error("기대 형식: EXPERT_ID: [ID]\nEXPERT_NAME: [NAME]\nEXPERT_EMAIL: [EMAIL]\nRECOMMENDATION: [REASON]");
                log.error("실제 응답: {}", gptResponse.substring(0, Math.min(300, gptResponse.length())));
            }
            
        } catch (Exception e) {
            log.error("🚨 GPT 응답 파싱 중 오류 발생", e);
        }
        
        log.warn("🔴 GPT 응답 파싱 실패 - 폴백 응답 사용");
        return result;
    }
    
    /**
     * 폴백 응답 생성 (GPT 실패 시)
     */
    private Map<String, Object> createFallbackResponse(String category) {
        Map<String, Object> response = new HashMap<>();
        
        log.warn("🚨 GPT API 실패로 폴백 응답 사용 - category: {}", category);
        
        // 해당 카테고리에 실제 전문가가 있는지 확인
        String targetSpecializationName = mapCategoryToSpecialization(category);
        List<ExpertProfile> categoryExperts = getCategoryExperts(category);
        
        if (categoryExperts.isEmpty()) {
            log.error("❌ 폴백 생성 시 {}({}) 분야 전문가가 없음", category, targetSpecializationName);
            
            String fallbackMessage = String.format(
                "죄송합니다. 현재 %s 분야에 활성화된 전문가가 없습니다. \n\n" +
                "다른 카테고리를 선택하시거나 나중에 다시 시도해주세요. 🙏",
                getCategoryName(category)
            );
            
            response.put("message", fallbackMessage);
            response.put("expertInfo", null);
            return response;
        }
        
        // 안전한 폴백: 처음 전문가 선택 (절대 다른 분야 전문가 선택 안함)
        ExpertProfile safeExpert = categoryExperts.get(0);
        
        log.info("🛡️ 안전 폴백 전문가 선택: {} (ID: {}) - 분야: {}", 
                safeExpert.getMember().getUsername(), 
                safeExpert.getMemberId(),
                getExpertSpecializationNames(safeExpert).stream().collect(Collectors.joining(", ")));
        
        String fallbackMessage = String.format(
            "임시로 %s 분야의 전문가를 추천드립니다. \n\n" +
            "더 정확한 추천을 원하시면 구체적인 요구사항을 " +
            "말씀해주시거나 잠시 후 다시 시도해주세요. 🚀",
            getCategoryName(category)
        );
        
        // 전문가 정보 추가
        Map<String, Object> expertInfo = new HashMap<>();
        expertInfo.put("id", safeExpert.getMemberId());
        expertInfo.put("name", safeExpert.getMember().getUsername());
        expertInfo.put("email", safeExpert.getMember().getEmail().getValue());
        expertInfo.put("profileUrl", "/expert-profiles/" + safeExpert.getMemberId());
        expertInfo.put("school", safeExpert.getSchool());
        expertInfo.put("major", safeExpert.getMajor());
        expertInfo.put("salary", safeExpert.getSalary().getValue());
        expertInfo.put("negoYn", safeExpert.getNegoYn().isNegotiable());
        expertInfo.put("introduceMessage", safeExpert.getIntroduceMessage());
        expertInfo.put("completedRequestCount", safeExpert.getCompletedRequestCount().getValue());
        expertInfo.put("recommendation", generateDefaultRecommendationReason(safeExpert, category));
        
        response.put("message", fallbackMessage);
        response.put("expertInfo", expertInfo);
        
        return response;
    }
    
    /**
     * 해당 카테고리의 전문가들 가져오기
     */
    private List<ExpertProfile> getCategoryExperts(String category) {
        String targetSpecializationName = mapCategoryToSpecialization(category);
        
        return expertProfileJPARepository.findAll().stream()
                .filter(ExpertProfile::isActivated)
                .filter(expert -> expert.getExpertSpecialization().stream()
                        .anyMatch(es -> {
                            return specializationJPARepository.findById(es.getId().getSpecializationId())
                                    .map(spec -> spec.getSpecializationName().getValue().equals(targetSpecializationName))
                                    .orElse(false);
                        }))
                .collect(Collectors.toList());
    }
    
    /**
     * 전문가의 전문분야 이름들 가져오기
     */
    private List<String> getExpertSpecializationNames(ExpertProfile expert) {
        return expert.getExpertSpecialization().stream()
                .map(es -> {
                    return specializationJPARepository.findById(es.getId().getSpecializationId())
                            .map(spec -> spec.getSpecializationName().getValue())
                            .orElse("알 수 없음");
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 전문가에 따른 AI 추천 이유 생성
     */
    private String generateRecommendationReason(ExpertProfile expert, String categoryName) {
        // 전문가의 실제 전문분야 확인
        String actualSpecializations = getExpertSpecializationNames(expert).stream()
                .collect(Collectors.joining(", "));
        String targetSpecializationName = mapCategoryToSpecialization(categoryName);
        
        // 전문분야 매칭 확인
        boolean isMatchingSpecialization = actualSpecializations.contains(targetSpecializationName);
        
        if (!isMatchingSpecialization) {
            log.warn("전문가 {}(전문분야: {})가 요청 카테고리({})와 매칭되지 않음. 기본 템플릿 사용.", 
                    expert.getMember().getUsername(), actualSpecializations, targetSpecializationName);
            return generateDefaultRecommendationReason(expert, categoryName);
        }
        
        try {
            // GPT를 활용해서 전문가 정보를 기반으로 추천사유 생성
            String gptGeneratedReason = generateRecommendationReasonWithGPT(expert, categoryName);
            
            if (gptGeneratedReason != null && !gptGeneratedReason.trim().isEmpty()) {
                // GPT 응답에 다른 분야 전문성이 언급되었는지 엄격히 검증
                String lowerResponse = gptGeneratedReason.toLowerCase();
                boolean containsWrongSpecialization = false;
                
                // 대상 전문분야가 아닌 다른 분야 용어들 검사
                String[] programmingWords = {"웹 개발", "앱 개발", "소프트웨어", "애플리케이션", "서버", "api", "데이터베이스", "알고리즘"};
                String[] designWords = {"디자인", "ui", "ux", "브랜드", "로고", "포스터", "타이포그래피", "3d 모델링"};
                String[] videoWords = {"영상", "편집", "모션", "애니메이션", "그래픽", "방송", "사운드", "추어이펙트"};
                String[] legalWords = {"세무", "법무", "노무", "회계", "법률", "계약서", "특허", "송무"};
                String[] translationWords = {"번역", "통역", "언어", "영어", "일본어", "중국어", "현지화", "국제"};
                
                // 타겟 전문분야가 아닌 다른 분야 용어 검사
                if (!targetSpecializationName.equals("프로그래밍")) {
                    for (String word : programmingWords) {
                        if (lowerResponse.contains(word.toLowerCase())) {
                            containsWrongSpecialization = true;
                            log.warn("잘못된 프로그래밍 용어 감지: {}", word);
                            break;
                        }
                    }
                }
                
                if (!targetSpecializationName.equals("디자인")) {
                    for (String word : designWords) {
                        if (lowerResponse.contains(word.toLowerCase())) {
                            containsWrongSpecialization = true;
                            log.warn("잘못된 디자인 용어 감지: {}", word);
                            break;
                        }
                    }
                }
                
                if (!targetSpecializationName.equals("영상편집")) {
                    for (String word : videoWords) {
                        if (lowerResponse.contains(word.toLowerCase())) {
                            containsWrongSpecialization = true;
                            log.warn("잘못된 영상편집 용어 감지: {}", word);
                            break;
                        }
                    }
                }
                
                if (!targetSpecializationName.equals("세무/법무/노무")) {
                    for (String word : legalWords) {
                        if (lowerResponse.contains(word.toLowerCase())) {
                            containsWrongSpecialization = true;
                            log.warn("잘못된 세무/법무/노무 용어 감지: {}", word);
                            break;
                        }
                    }
                }
                
                if (!targetSpecializationName.equals("번역/통역")) {
                    for (String word : translationWords) {
                        if (lowerResponse.contains(word.toLowerCase())) {
                            containsWrongSpecialization = true;
                            log.warn("잘못된 번역/통역 용어 감지: {}", word);
                            break;
                        }
                    }
                }
                
                if (containsWrongSpecialization) {
                    log.warn("전문가 {}(ID: {})의 GPT 응답에 잘못된 전문분야 언급 감지. 기본 템플릿 사용. 응답: {}", 
                            expert.getMember().getUsername(), expert.getMemberId(),
                            gptGeneratedReason.substring(0, Math.min(100, gptGeneratedReason.length())));
                    return generateDefaultRecommendationReason(expert, categoryName);
                }
                
                log.debug("GPT 추천사유 생성 성공 - expertId: {}", expert.getMemberId());
                return gptGeneratedReason;
            }
            
            log.warn("GPT 추천사유 생성 실패, 기본 템플릿 사용 - expertId: {}", expert.getMemberId());
            
        } catch (Exception e) {
            log.error("GPT 추천사유 생성 중 오류 발생 - expertId: {}", expert.getMemberId(), e);
        }
        
        // 폴백: 기본 템플릿 사용
        return generateDefaultRecommendationReason(expert, categoryName);
    }
    
    /**
     * 자기소개 기반 추천사유 검증 로직 강화
     */
    private String generateRecommendationReasonWithGPT(ExpertProfile expert, String categoryName) {
        try {
            // 전문가의 자기소개가 없으면 기본 템플릿 사용
            if (expert.getIntroduceMessage() == null || expert.getIntroduceMessage().trim().isEmpty()) {
                log.debug("전문가 자기소개가 비어있어 기본 템플릿 사용 - expertId: {}", expert.getMemberId());
                return generateDefaultRecommendationReason(expert, categoryName);
            }
            
            // 자기소개 내용 분석
            String introduction = expert.getIntroduceMessage().toLowerCase();
            
            // 전문가 정보를 기반으로 GPT 프롬프트 생성
            String prompt = buildRecommendationPrompt(expert, categoryName);
            
            // GPT API 호출
            GptRequestDto request = GptRequestDto.createRecommendationRequest(prompt);
            GptResponseDto response = gptApiClient.sendRecommendationRequest(request);
            
            if (response.isSuccessful()) {
                String generatedReason = response.getOutputText().trim();
                
                // 응답이 너무 길면 150자로 제한
                if (generatedReason.length() > 150) {
                    generatedReason = generatedReason.substring(0, 147) + "...";
                }
                
                // ✨ 강화된 검증: 자기소개와 생성된 추천사유의 일치성 검사
                if (isRecommendationAlignedWithIntroduction(expert.getIntroduceMessage(), generatedReason)) {
                    log.debug("GPT 추천사유 생성 및 검증 성공 - expertId: {}", expert.getMemberId());
                    return generatedReason;
                } else {
                    log.warn("생성된 추천사유가 자기소개와 일치하지 않아 기본 템플릿 사용 - expertId: {}", expert.getMemberId());
                    return generateDefaultRecommendationReason(expert, categoryName);
                }
                
            } else {
                log.warn("GPT API 응답 비정상 - expertId: {}", expert.getMemberId());
            }
            
        } catch (GptApiException e) {
            log.warn("GPT API 호출 실패 - expertId: {}, error: {}", expert.getMemberId(), e.getMessage());
        } catch (Exception e) {
            log.error("GPT 추천사유 생성 중 예상치 못한 오류 - expertId: {}", expert.getMemberId(), e);
        }
        
        return generateDefaultRecommendationReason(expert, categoryName);
    }
    
    /**
     * 추천사유 생성용 GPT 프롬프트 구성 (자기소개 중심으로 개선)
     */
    private String buildRecommendationPrompt(ExpertProfile expert, String categoryName) {
        // 전문가의 전문분야 가져오기
        String specializations = getExpertSpecializationNames(expert).stream()
                .collect(Collectors.joining(", "));
        
        String introduceMessage = expert.getIntroduceMessage() != null ? expert.getIntroduceMessage() : "";
        
        return String.format("""
            당신은 전문가 추천 전문 AI 어시스턴트입니다.
            
            ⚠️ **핵심 원칙**: 반드시 전문가의 자기소개 내용만을 기반으로 추천 이유를 작성하세요.
            
            **전문가 자기소개:**
            "%s"
            
            **전문가 기본 정보:**
            - 이름: %s
            - 전문분야: %s
            - 학력: %s %s 전공
            - 완료 의뢰: %d건
            - 희망 수수료: %d만원 (협상 %s)
            
            **절대 엄수 규칙:**
            1. 🎯 자기소개에 명시된 내용만 사용하여 추천 이유 작성
            2. 🚫 자기소개에 없는 기술이나 전문성은 절대 언급 금지
            3. 🚫 "웹 개발", "앱 개발", "소프트웨어", "디자인", "영상", "번역", "세무", "법무" 등 다른 분야 용어 사용 금지 (자기소개에 명시되어 있지 않은 경우)
            4. 📝 80-120자 내에서 간결하게 작성
            5. 💡 "~전문성을 가지고 있어", "~경험을 바탕으로 도움을 드릴 수 있어" 형태로 작성
            6. 🔍 자기소개가 비어있으면 "해당 분야에서 신뢰할 수 있는 전문가"로만 언급
            
            **좋은 예시:**
            - 자기소개: "드론 촬영 전문가로 5년간 항공 영상 제작 경험이 있습니다"
            - 추천 이유: "드론 촬영 전문가로 5년간의 항공 영상 제작 경험을 바탕으로 고품질 영상 제작을 도와드릴 수 있습니다."
            
            **나쁜 예시:**
            - 자기소개: "드론 촬영 전문가로 5년간 항공 영상 제작 경험이 있습니다"
            - 잘못된 추천 이유: "웹 개발 전문성을 바탕으로 브랜드 가치를 높이는 웹사이트를 완성해드릴 수 있습니다" ❌
            
            **추천사유 작성:**
            위 자기소개를 바탕으로만 추천 이유를 작성해주세요:
            """, 
            introduceMessage,
            expert.getMember().getUsername(),
            specializations,
            expert.getSchool(),
            expert.getMajor(),
            expert.getCompletedRequestCount().getValue(),
            expert.getSalary().getValue(),
            expert.getNegoYn().isNegotiable() ? "가능" : "불가능"
        );
    }
    
    /**
     * 자기소개 기반 기본 추천사유 생성 (폴백용)
     */
    private String generateDefaultRecommendationReason(ExpertProfile expert, String categoryName) {
        String introduction = expert.getIntroduceMessage();
        
        if (introduction != null && !introduction.trim().isEmpty()) {
            // 자기소개가 있는 경우: 자기소개 + 완료 의뢰 수 조합
            String trimmedIntro = introduction.length() > 60 ? 
                    introduction.substring(0, 57) + "..." : introduction.trim();
            
            return String.format("%s %d건의 의뢰를 성공적으로 완료하신 신뢰할 수 있는 전문가입니다.",
                    trimmedIntro, expert.getCompletedRequestCount().getValue());
        } else {
            // 자기소개가 없는 경우: 기본 템플릿
            String targetSpecializationName = mapCategoryToSpecialization(categoryName);
            return String.format("%s 분야에서 %d건의 의뢰를 성공적으로 완료한 신뢰할 수 있는 전문가입니다.", 
                    targetSpecializationName, expert.getCompletedRequestCount().getValue());
        }
    }
    
    /**
     * 추천사유와 자기소개의 일치성 검증
     */
    private boolean isRecommendationAlignedWithIntroduction(String introduction, String recommendation) {
        if (introduction == null || recommendation == null) {
            return false;
        }
        
        String introLower = introduction.toLowerCase();
        String recomLower = recommendation.toLowerCase();
        
        // 자기소개에서 핵심 키워드 추출
        Set<String> introKeywords = extractKeywords(introLower);
        Set<String> recomKeywords = extractKeywords(recomLower);
        
        // 추천사유의 키워드가 자기소개의 키워드와 얼마나 겹치는지 확인
        long matchingKeywords = recomKeywords.stream()
                .filter(introKeywords::contains)
                .count();
        
        // 최소 1개 이상의 키워드가 일치해야 함
        boolean hasMatchingKeywords = matchingKeywords > 0;
        
        // 금지된 키워드 검사 (자기소개에 없는 다른 분야 키워드)
        boolean hasNoForbiddenKeywords = !containsForbiddenKeywords(introduction, recommendation);
        
        log.debug("추천사유 검증 - 매칭 키워드: {}, 금지 키워드 없음: {}", 
                matchingKeywords, hasNoForbiddenKeywords);
        
        return hasMatchingKeywords && hasNoForbiddenKeywords;
    }
    
    /**
     * 텍스트에서 핵심 키워드 추출
     */
    private Set<String> extractKeywords(String text) {
        Set<String> keywords = new HashSet<>();
        
        // 기술/분야 관련 키워드들
        String[] techKeywords = {
            "드론", "촬영", "영상", "편집", "디자인", "로고", "브랜드", "웹", "앱", "소프트웨어",
            "프로그래밍", "개발", "번역", "통역", "세무", "법무", "노무", "회계", "마케팅",
            "항공", "3d", "모션", "그래픽", "ui", "ux", "모바일", "서버", "데이터베이스"
        };
        
        for (String keyword : techKeywords) {
            if (text.contains(keyword)) {
                keywords.add(keyword);
            }
        }
        
        return keywords;
    }
    
    /**
     * 자기소개에 없는 금지된 키워드가 추천사유에 포함되어 있는지 검사
     */
    private boolean containsForbiddenKeywords(String introduction, String recommendation) {
        String introLower = introduction.toLowerCase();
        String recomLower = recommendation.toLowerCase();
        
        // 다른 분야 키워드들 정의
        Map<String, String[]> fieldKeywords = Map.of(
            "프로그래밍", new String[]{"웹 개발", "웹사이트", "앱 개발", "소프트웨어", "애플리케이션", "서버", "api", "데이터베이스", "알고리즘", "코딩"},
            "디자인", new String[]{"ui 디자인", "ux 디자인", "브랜드 디자인", "로고 디자인", "포스터", "타이포그래피", "3d 모델링"},
            "영상편집", new String[]{"영상 제작", "영상 편집", "모션 그래픽", "애니메이션", "방송", "사운드 편집"},
            "세무법무", new String[]{"세무", "법무", "노무", "회계", "법률", "계약서", "특허", "송무"},
            "번역통역", new String[]{"번역", "통역", "언어", "영어", "일본어", "중국어", "현지화", "국제"}
        );
        
        // 각 분야별로 자기소개에 없는 키워드가 추천사유에 있는지 확인
        for (String[] keywords : fieldKeywords.values()) {
            for (String keyword : keywords) {
                // 자기소개에는 없지만 추천사유에는 있는 키워드 발견
                if (!introLower.contains(keyword.toLowerCase()) && recomLower.contains(keyword.toLowerCase())) {
                    log.warn("금지된 키워드 발견: '{}' (자기소개에 없음)", keyword);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 카테고리를 전문분야명으로 매핑 (내부 사용)
     */
    private String mapCategoryToSpecialization(String category) {
        return switch (category) {
            case "design" -> "디자인";
            case "programming" -> "프로그래밍";
            case "video" -> "영상편집";
            case "legal" -> "세무/법무/노무";
            case "translation" -> "번역/통역";
            default -> category;
        };
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
