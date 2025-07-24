package com.example.ium.member.application.service;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium.member.application.dto.request.ExpertProfileFormDto;
import com.example.ium.member.application.dto.response.ExpertProfileViewDto;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.model.expert.Attachment;
import com.example.ium.member.domain.model.expert.ExpertProfile;
import com.example.ium.member.domain.model.expert.ExpertSpecialization;
import com.example.ium.member.domain.repository.ExpertProfileJPARepository;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.member.domain.service.AttachmentService;
import com.example.ium.specialization.domain.repository.SpecializationJPARepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExpertProfileService {

    private final MemberMetaCommandService memberMetaCommandService;
    private final AttachmentService attachmentService;

    private final ExpertProfileJPARepository expertProfileJPARepository;
    private final MemberJPARepository memberJPARepository;
    private final SpecializationJPARepository specializationJPARepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * 전문가 프로필을 생성합니다.
     *
     * @param requestDto 전문가 프로필 생성 요청 DTO
     * @param memberId 전문가 프로필을 생성할 멤버의 ID
     */
    @Transactional
    public void createExpertProfile(ExpertProfileFormDto requestDto, Long memberId) {

        // 전문가 프로필이 이미 존재하는지 확인합니다.
        validateDuplicateExpertProfile(memberId);
        // 멤버 ID로 멤버를 조회합니다.
        Member member = getMember(memberId);

        // 전문가 프로필을 생성합니다.
        ExpertProfile newExpertProfile = ExpertProfile.createExpertProfile(
                member,
                requestDto.getIntroduceMessage(),
                requestDto.getPortfolioDescription(),
                requestDto.getSchool(),
                requestDto.getMajor(),
                requestDto.getStartCareerDate(),
                requestDto.getSalary(),
                requestDto.isNegoYn()
        );

        // 전문가 프로필에 대한 전문 분야를 생성합니다.
        List<ExpertSpecialization> expertSpecializations = createExpertSpecializations(requestDto, newExpertProfile);
        for (ExpertSpecialization specialization : expertSpecializations) {
            newExpertProfile.addExpertSpecialization(specialization);
        }

        if (requestDto.getAttachments() != null) {
            List<Attachment> attachments = attachmentService.saveAttachments(requestDto.getAttachments(), newExpertProfile);
            for (Attachment attachment : attachments) {
                newExpertProfile.addAttachment(attachment);
            }
        }

        em.persist(newExpertProfile);

        // 전문가 프로필 생성 후 멤버 메타 캐시 업데이트
        memberMetaCommandService.cacheMemberMeta(member.getEmail().getValue());
    }

    /**
     * 전문가 프로필을 조회합니다.
     *
     * @param memberId 전문가 프로필의 멤버 ID
     * @return 전문가 프로필 DTO
     */
    public ExpertProfileViewDto getExpertProfile(Long memberId) {
        ExpertProfile expertProfile = findExpertProfile(memberId);
        List<Long> specializationIds = expertProfile.getExpertSpecialization().stream()
                .map(specialization -> specialization.getId().getSpecializationId())
                .toList();
        List<ExpertProfileViewDto.SpecializationSummary> specializationSummaries =
                specializationJPARepository.findAllById(specializationIds).stream()
                        .map(s -> new ExpertProfileViewDto.SpecializationSummary(s.getId(), s.getSpecializationName().getValue()))
                        .toList();
        List<ExpertProfileViewDto.AttachmentInfo> attachmentInfos = expertProfile.getAttachments().stream()
                .map(attachment -> new ExpertProfileViewDto.AttachmentInfo(attachment.getFileName(), attachment.getFileUrl(), attachment.getFileType()))
                .toList();
        return new ExpertProfileViewDto(
                expertProfile.getMemberId(),
                expertProfile.getIntroduceMessage(),
                expertProfile.getPortfolioDescription(),
                expertProfile.getSchool(),
                expertProfile.getMajor(),
                expertProfile.getCareerDate().getStartDate(),
                expertProfile.getSalary().getValue(),
                expertProfile.getNegoYn().getValue(),
                expertProfile.getCompletedRequestCount().getValue(),
                specializationSummaries,
                attachmentInfos
        );
    }

    /**
     * 전문가 프로필을 활성화합니다.
     *
     * @param memberId 전문가 프로필의 멤버 ID
     */
    @Transactional
    public void activateExpertProfile(Long memberId) {
        ExpertProfile expertProfile = findExpertProfile(memberId);
        expertProfile.activate();
    }

    /**
     * 전문가 프로필을 비활성화합니다.
     *
     * @param memberId 전문가 프로필의 멤버 ID
     */
    @Transactional
    public void deactivateProfile(Long memberId) {
        ExpertProfile expertProfile = findExpertProfile(memberId);
        expertProfile.deactivate();
    }

    /**
     * 전문가 프로필이 이미 존재하는지 확인합니다.
     *
     * @param memberId 전문가 프로필을 생성할 멤버의 ID
     */
    private void validateDuplicateExpertProfile(Long memberId) {
        if (expertProfileJPARepository.existsById(memberId)) {
            throw new IumApplicationException(ErrorCode.EXPERT_PROFILE_ALREADY_EXISTS);
        }
    }

    /**
     * 멤버 ID로 멤버를 조회합니다.
     *
     * @param memberId 조회할 멤버의 ID
     * @return 조회된 멤버
     */
    private Member getMember(Long memberId) {
        return memberJPARepository.findById(memberId)
                .orElseThrow(() -> new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 전문가 프로필에 대한 전문 분야를 생성합니다.
     *
     * @param requestDto 전문가 프로필 생성 요청 DTO
     * @param expertProfile 전문가 프로필
     * @return 생성된 전문가 전문 분야 리스트
     */
    private List<ExpertSpecialization> createExpertSpecializations(ExpertProfileFormDto requestDto, ExpertProfile expertProfile) {
        return requestDto.getSpecializationIds().stream()
                .map(specializationId -> ExpertSpecialization.createExpertSpecialization(expertProfile, specializationId))
                .toList();
    }

    /**
     * 전문가 프로필을 멤버 ID로 조회합니다.
     *
     * @param memberId 전문가 프로필의 멤버 ID
     * @return 전문가 프로필
     */
    private ExpertProfile findExpertProfile(Long memberId) {
        return expertProfileJPARepository.findByIdByEagerLoading(memberId)
                .orElseThrow(() -> new IumApplicationException(ErrorCode.EXPERT_PROFILE_NOT_FOUND));
    }

    /**
     * 전문가 프로필이 활성화되어 있는지 확인합니다.
     *
     * @param memberId 전문가 프로필의 멤버 ID
     * @return 전문가 프로필 활성화 상태
     */
    public boolean isExpertProfileActivated(Long memberId) {
        Optional<Boolean> cachedActivationStatus = memberMetaCommandService.getExpertProfileActivationStatus(memberId);
        return cachedActivationStatus.orElseGet(() -> expertProfileJPARepository.findById(memberId)
                .map(ExpertProfile::isActivated)
                .orElse(false));
    }
}
