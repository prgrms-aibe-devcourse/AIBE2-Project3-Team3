package com.example.ium._core.security;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium.member.domain.model.Email;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.model.Status;
import com.example.ium.member.domain.repository.MemberJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberJPARepository memberJPARepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Member member = memberJPARepository.findByEmail(Email.of(email))
                .orElseThrow(() -> new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND));

        if (!member.getStatus().equals(Status.ACTIVE)) {
            throw new IumApplicationException(ErrorCode.MEMBER_NOT_ACTIVE);
        }

        return new CustomUserDetails(
                member.getId(),
                member.getEmail().getValue(),
                member.getPassword().getValue(),
                List.of(new SimpleGrantedAuthority(member.getRole().getKey()))
        );
    }
}
