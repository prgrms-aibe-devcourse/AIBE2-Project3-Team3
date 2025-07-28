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
        log.debug("상대 전문분야명: {}", targetSpecializationName);
        
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
            
            return "해당 카테고리의 전문가가 없습니다.";
        }
        
        // 전체 활성화된 전문가 수 확인
        List<ExpertProfile> allActiveExperts = expertProfileJPARepository.findAll().stream()
                .filter(ExpertProfile::isActivated)
                .collect(Collectors.toList());
        log.debug("전체 활성화된 전문가 수: {}", allActiveExperts.size());
        
        // 해당 전문분야의 활성화된 전문가들 조회
        List<ExpertProfile> categoryExperts = allActiveExperts.stream()
                .filter(expert -> hasMatchingSpecialization(expert, targetSpecializations))
                .collect(Collectors.toList());
        
        log.debug("카테고리에 매칭되는 전문가 수: {}", categoryExperts.size());
        
        if (categoryExperts.isEmpty()) {
            log.warn("카테고리에 해당하는 활성화된 전문가가 없습니다: {}", category);
            
            // 전체 전문가들의 전문분야 로깅
            for (ExpertProfile expert : allActiveExperts) {
                String expertSpecializations = expert.getExpertSpecialization().stream()
                        .map(es -> specializationJPARepository.findById(es.getId().getSpecializationId())
                                .map(spec -> spec.getSpecializationName().getValue())
                                .orElse("알 수 없음"))
                        .collect(Collectors.joining(", "));
                log.debug("전문가 {} (ID: {})의 전문분야: {}", 
                        expert.getMember().getUsername(), expert.getMemberId(), expertSpecializations);
            }
            
            return "해당 카테고리의 활성화된 전문가가 없습니다.";
        }
        
        StringBuilder expertsData = new StringBuilder();
        expertsData.append("활성화된 전문가들 목록:\n");
        
        for (ExpertProfile expert : categoryExperts) {
            expertsData.append("\n전문가 ID: ").append(expert.getMemberId()).append("\n");
            expertsData.append("- 이름: ").append(expert.getMember().getUsername()).append("\n");
            expertsData.append("- 이메일: ").append(expert.getMember().getEmail().getValue()).append("\n");
            expertsData.append("- 소개: ").append(expert.getIntroduceMessage()).append("\n");
            expertsData.append("- 포트폴리오: ").append(expert.getPortfolioDescription()).append("\n");
            expertsData.append("- 학교: ").append(expert.getSchool()).append("\n");
            expertsData.append("- 전공: ").append(expert.getMajor()).append("\n");
            expertsData.append("- 경력 시작일: ").append(expert.getCareerDate().getStartDate()).append("\n");
            expertsData.append("- 희망 연봉: ").append(expert.getSalary().getValue()).append("만원\n");
            expertsData.append("- 협상 가능: ").append(expert.getNegoYn().isNegotiable() ? "가능" : "불가능").append("\n");
            expertsData.append("- 완료 의뢰 수: ").append(expert.getCompletedRequestCount().getValue()).append("건\n");
            
            // 전문분야 정보 추가
            String specializations_str = expert.getExpertSpecialization().stream()
                    .map(es -> {
                        return specializationJPARepository.findById(es.getId().getSpecializationId())
                                .map(spec -> spec.getSpecializationName().getValue())
                                .orElse("알 수 없음");
                    })
                    .collect(Collectors.joining(", "));
            expertsData.append("- 전문분야: ").append(specializations_str).append("\n");
        }
        
        log.debug("전문가 프로필 데이터 수집 완료 - category: {}, 전문가 수: {}", category, categoryExperts.size());
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
            case "legal" -> "세무/법무/노무";  // 정확한 전문분야명으로 수정
            case "translation" -> "번역/통역";  // 정확한 전문분야명으로 수정
            default -> category;
        };
    }
}
