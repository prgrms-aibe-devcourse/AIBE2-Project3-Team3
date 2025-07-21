package com.example.ium.member.application.service;

import com.example.ium.member.application.dto.mapper.MemberMetaMapper;
import com.example.ium.member.domain.meta.MemberMeta;
import com.example.ium.member.domain.repository.MemberJPARepository;
import com.example.ium.member.infrastructure.repository.MemberMetaProjection;
import com.example.ium.member.infrastructure.repository.MemberMetaRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberMetaCommandService {

    private final MemberMetaRedisRepository memberMetaRedisRepository;
    private final MemberJPARepository memberJPARepository;

    public void cacheMemberMeta(String email) {
        List<MemberMetaProjection> projections = memberJPARepository.findMemberMetaByEmail(email);
        if (projections == null || projections.isEmpty()) {
            return;
        }

        MemberMeta memberMeta = MemberMetaMapper.toMemberMeta(projections);
        if (memberMeta == null) {
            return;
        }

        memberMetaRedisRepository.save(memberMeta);
    }
}
