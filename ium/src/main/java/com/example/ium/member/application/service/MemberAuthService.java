package com.example.ium.member.application.service;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium.member.application.dto.request.SignUpRequestDto;
import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.model.Password;
import com.example.ium.member.domain.repository.MemberJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberAuthService {

    private final MemberJPARepository memberJPARepository;

    private final PasswordEncoder passwordEncoder;

    /*
     * 회원 가입
     * @param requestDto 회원 가입 요청 DTO
     * 이메일 중복 검사
     * 비밀번호 암호화
     * 회원 엔티티 생성 후 저장
     */
    @Transactional
    public void signUp(SignUpRequestDto requestDto) {

        Email email = Email.of(requestDto.email());
        validateDuplicatedEmail(email);

        Password password = Password.encode(requestDto.password(), passwordEncoder);
        Member member = Member.createMember(requestDto.username(), email, password);

        // 회원 저장
        memberJPARepository.save(member);
    }

    /*
     * 이메일 중복 검사
     *
     * @param email 회원 이메일
     */
    private void validateDuplicatedEmail(Email email) {
        if (memberJPARepository.existsByEmail(email)) {
            throw new IumApplicationException(ErrorCode.DUPLICATE_EMAIL);
        }
    }


    /*
     * 이메일로 회원 조회
     *
     * @param email 회원 이메일
     * @return 회원 엔티티
     */
    private Member findMemberByEmail(String email) {
        return memberJPARepository.findByEmail(Email.of(email))
                .orElseThrow(() -> new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
