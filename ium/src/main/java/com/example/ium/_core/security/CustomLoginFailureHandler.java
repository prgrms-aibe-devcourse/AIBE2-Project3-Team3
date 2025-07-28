package com.example.ium._core.security;

import com.example.ium._core.exception.IumApplicationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {
        String errorMessage = URLEncoder.encode("아이디 또는 비밀번호가 잘못되었습니다.", StandardCharsets.UTF_8);

        if (exception.getCause() instanceof IumApplicationException ex) {
            errorMessage = ex.getErrorCode().getMessage();
        }

        response.sendRedirect("/login?errorMsg=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
    }
}