package com.example.ium.recommend.domain.service;

import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.model.Role;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.member.application.service.ExpertProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 회원 역할 확인 서비스
 * 회원의 실제 역할을 판단 (USER/EXPERT)
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MemberRoleService {
    
    private final MemberJPARepository memberJPARepository;
    private final ExpertProfileService expertProfileService;
    
    /**
     * 회원의 실제 역할 반환
     * 
     * @param memberId 회원 ID
     * @return 실제 역할 (USER/EXPERT)
     */
    public String getMemberRole(Long memberId) {
        log.debug("회원 역할 확인 시작 - memberId: {}", memberId);
        
        Member member = memberJPARepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + memberId));
        
        // 기본 역할이 EXPERT인 경우, 실제 전문가 프로필이 활성화되어 있는지 확인
        if (member.getRole() == Role.EXPERT) {
            boolean isExpertActivated = expertProfileService.isExpertProfileActivated(memberId);
            if (isExpertActivated) {
                log.debug("활성화된 전문가 프로필 확인 - memberId: {}", memberId);
                return Role.EXPERT.name();
            } else {
                log.debug("비활성화된 전문가 프로필, USER로 처리 - memberId: {}", memberId);
                return Role.USER.name();
            }
        }
        
        // 기본적으로 USER 역할
        log.debug("일반 사용자 확인 - memberId: {}", memberId);
        return Role.USER.name();
    }
    
    /**
     * 회원이 전문가인지 확인
     * 
     * @param memberId 회원 ID
     * @return 전문가 여부
     */
    public boolean isExpert(Long memberId) {
        return Role.EXPERT.name().equals(getMemberRole(memberId));
    }
    
    /**
     * 회원이 일반 사용자인지 확인
     * 
     * @param memberId 회원 ID
     * @return 일반 사용자 여부
     */
    public boolean isUser(Long memberId) {
        return Role.USER.name().equals(getMemberRole(memberId));
    }
}
