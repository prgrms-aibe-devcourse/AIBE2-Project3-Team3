package com.example.ium.member.application.service;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium.member.domain.model.expert.ExpertProfile;
import com.example.ium.member.domain.repository.ExpertProfileJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExpertProfileService {

    private final ExpertProfileJPARepository expertProfileJPARepository;

    /**
     * 전문가 프로필을 활성화합니다.
     *
     * @param memberId 전문가 프로필의 멤버 ID
     */
    @Transactional
    public void activateExpertProfile(Long memberId) {
        ExpertProfile expertProfile = expertProfileJPARepository.findById(memberId)
                .orElseThrow(() -> new IumApplicationException(ErrorCode.EXPERT_PROFILE_NOT_FOUND));
        expertProfile.activate();
    }

    /**
     * 전문가 프로필을 비활성화합니다.
     *
     * @param memberId 전문가 프로필의 멤버 ID
     */
    @Transactional
    public void deactivateProfile(Long memberId) {
        ExpertProfile expertProfile = expertProfileJPARepository.findById(memberId)
                .orElseThrow(() -> new IumApplicationException(ErrorCode.EXPERT_PROFILE_NOT_FOUND));
        expertProfile.deactivate();
    }
}
