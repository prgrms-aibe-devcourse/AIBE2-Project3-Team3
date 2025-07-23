package com.example.ium._core.config;

import com.example.ium._core.security.CustomAuthenticationEntryPoint;
import com.example.ium._core.security.CustomLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    public SecurityConfig(CustomLoginSuccessHandler customLoginSuccessHandler) {
        this.customLoginSuccessHandler = customLoginSuccessHandler;
    }

    private static final String[] TEMPLATE_LIST = {
            "/favicon.ico",
            "/css/**",
            "/js/**",
            "/images/**",
            "/signup",
            "/login",
            "/workrequest.css"
    };

    // 화이트리스트 정의
    private static final String[] WHITE_LIST = {
            "/",
            "/error",
            "/api/auth/**",
            "/api/swagger-ui/**",
            "/api/health/**",
            "/api/actuator/**",
            "/h2-console/**",
            "/chat/**",
            "/ws/**",
            "/recommend/**",
            "/css/**",
            "/js/**",
            "/img/**"
    };

    // 관리자 리스트 정의
    private static final String[] ADMIN_LIST = {
            "/api/admin/**"
    };

    /*
        * HTTP 보안 설정
        * 이 설정은 HTTP 요청에 대한 보안 규칙을 정의합니다.
        * - 화이트리스트에 있는 경로는 인증 없이 접근 가능
        * - 관리자 리스트에 있는 경로는 "ADMIN" 역할을 가진 사용자만 접근 가능
        * - 나머지 요청은 인증된 사용자만 접근 가능
        * - 로그인 페이지와 로그인 처리 URL을 설정
        * - 로그아웃 시 성공 URL을 설정
        * - HTTP 기본 인증을 사용하도록 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .defaultsDisabled()
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(TEMPLATE_LIST).permitAll()
                        .requestMatchers(WHITE_LIST).permitAll()
                        .requestMatchers(ADMIN_LIST).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/authenticate")
                        .usernameParameter("user")
                        .passwordParameter("pwd")
                        .successHandler(customLoginSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/"))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }


    // BCrypt 인코더 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}