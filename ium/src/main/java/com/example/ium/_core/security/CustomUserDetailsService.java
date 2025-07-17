package com.example.ium._core.security;

import com.example.ium._core.exception.ErrorCode;
import com.example.ium._core.exception.IumApplicationException;
import com.example.ium.member.domain.model.Member;
import com.example.ium.member.domain.repository.MemberJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
        Member member = memberJPARepository.findByEmail(email)
                .orElseThrow(() -> new IumApplicationException(ErrorCode.MEMBER_NOT_FOUND));

        return new User(
                member.getEmail(),
                member.getPassword(),
                List.of(new SimpleGrantedAuthority(member.getRole().getKey()))
        );
    }
}
