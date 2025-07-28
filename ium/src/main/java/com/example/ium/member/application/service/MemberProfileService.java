package com.example.ium.member.application.service;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium.member.application.dto.response.MemberProfileViewDto;
import com.example.ium.member.domain.repository.MemberJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberProfileService {

    private final MemberJPARepository memberJPARepository;

    public MemberProfileViewDto getMemberProfile(Long memberId) {
        return memberJPARepository.findById(memberId)
                .map(member -> new MemberProfileViewDto(
                        member.getId(),
                        member.getUsername(),
                        member.getEmail().getValue()
                ))
                .orElseThrow(() -> new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
