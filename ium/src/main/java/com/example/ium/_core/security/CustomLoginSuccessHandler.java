package com.example.ium._core.security;

import com.example.ium.member.application.service.MemberMetaCommandService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberMetaCommandService memberMetaCommandService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 로그인 성공 시, 사용자 메타데이터를 Redis에 저장
        String email = authentication.getName();
        memberMetaCommandService.cacheMemberMeta(email);

        // 로그인 성공 후, 기본 페이지로 리다이렉트
        response.sendRedirect("/");
    }
}
