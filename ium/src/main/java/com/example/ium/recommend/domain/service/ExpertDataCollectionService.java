package com.example.ium.recommend.domain.service;

import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.model.expert.ExpertProfile;
import com.example.ium.member.domain.repository.ExpertProfileJPARepository;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.specialization.domain.model.Specialization;
import com.example.ium.specialization.domain.repository.SpecializationJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 전문가 관련 데이터 수집 서비스
 * GPT로 전송할 전문가 및 사용자 데이터를 수집하고 가공
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ExpertDataCollectionService {
    
    private final MemberJPARepository memberJPARepository;
    private final ExpertProfileJPARepository expertProfileJPARepository;
    private final SpecializationJPARepository specializationJPARepository;
    
    /**
     * 사용자 프로필 데이터 수집
     * 
     * @param memberId 사용자 ID
     * @return GPT용 사용자 프로필 데이터 문자열
     */
    public String collectUserProfileData(Long memberId) {
        log.debug("사용자 프로필 데이터 수집 시작 - memberId: {}", memberId);
        
        Member member = memberJPARepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + memberId));
        
        StringBuilder userProfileData = new StringBuilder();
        userProfileData.append("사용자 정보:\n");
        userProfileData.append("- 사용자명: ").append(member.getUsername()).append("\n");
        userProfileData.append("- 역할: ").append(member.getRole().name()).append("\n");
        
        log.debug("사용자 프로필 데이터 수집 완료 - memberId: {}", memberId);
        return userProfileData.toString();
    }
    
    /**
     * 특정 카테고리의 전문가들 프로필 데이터 수집
     * 
     * @param category 카테고리
     * @return GPT용 전문가들 프로필 데이터 문자열
     */
    public String collectExpertProfilesData(String category) {
        log.debug("전문가 프로필 데이터 수집 시작 - category: {}", category);
        
        // 카테고리를 전문분야명으로 매핑
        String targetSpecializationName = mapCategoryToSpecialization(category);
        log.debug("타겟 전문분야명: {}", targetSpecializationName);
        
        // 카테고리에 해당하는 전문분야 조회
        List<Specialization> targetSpecializations = specializationJPARepository.findAll().stream()
                .filter(spec -> spec.getSpecializationName().getValue().equals(targetSpecializationName))
                .collect(Collectors.toList());
        
        log.debug("찾은 전문분야 수: {}", targetSpecializations.size());
        
        if (targetSpecializations.isEmpty()) {
            log.warn("카테고리에 해당하는 전문분야를 찾을 수 없습니다: {} -> {}", category, targetSpecializationName);
            
            // 전체 전문분야 로깅
            List<String> allSpecializations = specializationJPARepository.findAll().stream()
                    .map(spec -> spec.getSpecializationName().getValue())
                    .collect(Collectors.toList());
            log.warn("전체 전문분야 목록: {}", allSpecializations);
            
            return String.format("ERROR: %s 카테고리(%s 전문분야)에 해당하는 전문가가 없습니다.", category, targetSpecializationName);
        }
        
        // 해당 전문분야의 활성화된 전문가들만 조회 (더 엄격한 필터링)
        List<ExpertProfile> categoryExperts = expertProfileJPARepository.findAll().stream()
                .filter(ExpertProfile::isActivated)
                .filter(expert -> hasMatchingSpecialization(expert, targetSpecializations))
                .collect(Collectors.toList());
        
        log.info("🔍 {}({}) 카테고리 전문가 필터링 결과:", category, targetSpecializationName);
        log.info("   - 전체 활성화된 전문가 수: {}", 
                expertProfileJPARepository.findAll().stream().filter(ExpertProfile::isActivated).count());
        log.info("   - 매칭되는 전문가 수: {}", categoryExperts.size());
        
        // 매칭된 전문가들 상세 로깅
        for (ExpertProfile expert : categoryExperts) {
            String expertSpecializations = expert.getExpertSpecialization().stream()
                    .map(es -> specializationJPARepository.findById(es.getId().getSpecializationId())
                            .map(spec -> spec.getSpecializationName().getValue())
                            .orElse("알 수 없음"))
                    .collect(Collectors.joining(", "));
            log.info("   ✅ 매칭: {} (ID: {}) - 전문분야: {}", 
                    expert.getMember().getUsername(), expert.getMemberId(), expertSpecializations);
        }
        
        log.debug("{}({}) 카테고리에 매칭되는 활성화된 전문가 수: {}", category, targetSpecializationName, categoryExperts.size());
        
        if (categoryExperts.isEmpty()) {
            log.error("❌ {}({}) 카테고리에 해당하는 활성화된 전문가가 없습니다.", category, targetSpecializationName);
            
            // 디버깅을 위해 전체 활성화된 전문가들의 전문분야 로깅
            List<ExpertProfile> allActiveExperts = expertProfileJPARepository.findAll().stream()
                    .filter(ExpertProfile::isActivated)
                    .collect(Collectors.toList());
            
            log.error("🔍 전체 활성화된 전문가 수: {}", allActiveExperts.size());
            log.error("📋 전체 활성화된 전문가들의 전문분야:");
            for (ExpertProfile expert : allActiveExperts) {
                String expertSpecializations = expert.getExpertSpecialization().stream()
                        .map(es -> specializationJPARepository.findById(es.getId().getSpecializationId())
                                .map(spec -> spec.getSpecializationName().getValue())
                                .orElse("알 수 없음"))
                        .collect(Collectors.joining(", "));
                log.error("   - {} (ID: {}): {}", 
                        expert.getMember().getUsername(), expert.getMemberId(), expertSpecializations);
            }
            
            return String.format("ERROR: %s 카테고리(%s 전문분야)에 해당하는 활성화된 전문가가 없습니다. " +
                    "전체 %d명의 활성화된 전문가 중 매칭되는 전문가가 없습니다.", 
                    category, targetSpecializationName, allActiveExperts.size());
        }
        
        // GPT에게 명확한 지시사항과 함께 전문가 데이터 제공
        StringBuilder expertsData = new StringBuilder();
        expertsData.append(String.format("===== %s 분야(%s) 전용 전문가 목록 =====\n", 
                getCategoryDisplayName(category), targetSpecializationName));
        expertsData.append(String.format("⚠️ 중요: 아래 %d명의 전문가는 모두 %s 분야 전문가입니다. 반드시 이 목록에서만 선택해야 합니다.\n\n", 
                categoryExperts.size(), targetSpecializationName));
        
        for (int i = 0; i < categoryExperts.size(); i++) {
            ExpertProfile expert = categoryExperts.get(i);
            expertsData.append(String.format("[전문가 %d/%d]\n", i + 1, categoryExperts.size()));
            expertsData.append("전문가 ID: ").append(expert.getMemberId()).append("\n");
            expertsData.append("이름: ").append(expert.getMember().getUsername()).append("\n");
            expertsData.append("이메일: ").append(expert.getMember().getEmail().getValue()).append("\n");
            expertsData.append("자기소개: ").append(expert.getIntroduceMessage() != null ? expert.getIntroduceMessage() : "없음").append("\n");
            expertsData.append("포트폴리오: ").append(expert.getPortfolioDescription() != null ? expert.getPortfolioDescription() : "없음").append("\n");
            expertsData.append("학력: ").append(expert.getSchool()).append(" ").append(expert.getMajor()).append("\n");
            expertsData.append("경력: ").append(expert.getCareerDate().getStartDate()).append(" 시작\n");
            expertsData.append("희망 연봉: ").append(expert.getSalary().getValue()).append("만원\n");
            expertsData.append("협상: ").append(expert.getNegoYn().isNegotiable() ? "가능" : "불가능").append("\n");
            expertsData.append("완료 의뢰: ").append(expert.getCompletedRequestCount().getValue()).append("건\n");
            
            // 전문분야 정보 추가 (검증용)
            String specializations_str = expert.getExpertSpecialization().stream()
                    .map(es -> specializationJPARepository.findById(es.getId().getSpecializationId())
                            .map(spec -> spec.getSpecializationName().getValue())
                            .orElse("알 수 없음"))
                    .collect(Collectors.joining(", "));
            expertsData.append("전문분야: ").append(specializations_str).append("\n");
            expertsData.append("\n========================================\n\n");
        }
        
        log.info("✅ {} 카테고리 전문가 데이터 수집 완료 - {}명의 {}({}) 전문가", 
                category, categoryExperts.size(), targetSpecializationName, category);
        return expertsData.toString();
    }
    
    /**
     * 특정 전문가의 프로필 데이터 수집
     * 
     * @param memberId 전문가 ID
     * @return GPT용 전문가 프로필 데이터 문자열
     */
    public String collectExpertProfileData(Long memberId) {
        log.debug("개별 전문가 프로필 데이터 수집 시작 - memberId: {}", memberId);
        
        ExpertProfile expert = expertProfileJPARepository.findByIdByEagerLoading(memberId)
                .orElseThrow(() -> new IllegalArgumentException("전문가 프로필을 찾을 수 없습니다: " + memberId));
        
        StringBuilder expertData = new StringBuilder();
        expertData.append("전문가 정보:\n");
        expertData.append("- 이름: ").append(expert.getMember().getUsername()).append("\n");
        expertData.append("- 이메일: ").append(expert.getMember().getEmail().getValue()).append("\n");
        expertData.append("- 소개: ").append(expert.getIntroduceMessage()).append("\n");
        expertData.append("- 포트폴리오: ").append(expert.getPortfolioDescription()).append("\n");
        expertData.append("- 학교: ").append(expert.getSchool()).append("\n");
        expertData.append("- 전공: ").append(expert.getMajor()).append("\n");
        expertData.append("- 경력 시작일: ").append(expert.getCareerDate().getStartDate()).append("\n");
        expertData.append("- 희망 연봉: ").append(expert.getSalary().getValue()).append("만원\n");
        expertData.append("- 협상 가능: ").append(expert.getNegoYn().isNegotiable() ? "가능" : "불가능").append("\n");
        expertData.append("- 완료 의뢰 수: ").append(expert.getCompletedRequestCount().getValue()).append("건\n");
        
        // 전문분야 정보 추가
        String specializations = expert.getExpertSpecialization().stream()
                .map(es -> {
                    return specializationJPARepository.findById(es.getId().getSpecializationId())
                            .map(spec -> spec.getSpecializationName().getValue())
                            .orElse("알 수 없음");
                })
                .collect(Collectors.joining(", "));
        expertData.append("- 전문분야: ").append(specializations).append("\n");
        
        log.debug("개별 전문가 프로필 데이터 수집 완료 - memberId: {}", memberId);
        return expertData.toString();
    }
    
    /**
     * 전문분야가 카테고리와 매칭되는지 확인
     */
    private boolean isMatchingCategory(String specializationName, String category) {
        String normalizedCategory = mapCategoryToSpecialization(category);
        // 정확한 매칭을 위해 equals 사용
        return specializationName.equals(normalizedCategory);
    }
    
    /**
     * 전문가가 해당 전문분야를 가지고 있는지 확인
     */
    private boolean hasMatchingSpecialization(ExpertProfile expert, List<Specialization> targetSpecializations) {
        return expert.getExpertSpecialization().stream()
                .anyMatch(es -> targetSpecializations.stream()
                        .anyMatch(spec -> spec.getId().equals(es.getId().getSpecializationId())));
    }
    
    /**
     * 카테고리를 전문분야명으로 매핑
     */
    private String mapCategoryToSpecialization(String category) {
        return switch (category) {
            case "design" -> "디자인";
            case "programming" -> "프로그래밍";
            case "video" -> "영상편집";
            case "legal" -> "세무/법무/노무";  // legal 카테고리는 세무/법무/노무 전문가만
            case "translation" -> "번역/통역";  // translation 카테고리는 번역/통역 전문가만
            default -> category;
        };
    }
    
    /**
     * 카테고리를 사용자 친화적 이름으로 변환
     */
    private String getCategoryDisplayName(String category) {
        return switch (category) {
            case "design" -> "디자인";
            case "programming" -> "프로그래밍";
            case "video" -> "영상편집";
            case "legal" -> "법무/세무/노무";
            case "translation" -> "번역/통역";
            default -> category;
        };
    }
}
