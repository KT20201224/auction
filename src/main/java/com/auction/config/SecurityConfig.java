package com.auction.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 클래스 - 인증 및 권한 관리를 담당합니다.
 */
@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * BCryptPasswordEncoder 빈 등록 (비밀번호 암호화)
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 관리자(AuthenticationManager) 빈 등록
     *
     * @param authenticationConfiguration Spring Security 인증 설정
     * @return AuthenticationManager 인스턴스
     * @throws Exception 인증 설정 오류 발생 시 예외
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 보안 필터 체인 설정
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 인스턴스
     * @throws Exception 보안 설정 오류 발생 시 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> auth
                        // 🔹 공용(비로그인) 접근 허용
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**").permitAll()
                        // 🔹 로그인한 사용자만 접근 가능
                        .requestMatchers("/dashboard", "/charge", "/charge-history", "/mypage").authenticated()
                        // 🔹 관리자 페이지 접근 제한
                        .requestMatchers("/admin/**").hasAuthority("ADMIN") // ✅ "ROLE_" prefix 필요 없음
                        // 🔹 기타 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // 커스텀 로그인 페이지 설정
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 홈으로 이동
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 처리 URL
                        .logoutSuccessUrl("/") // 로그아웃 후 홈으로 이동
                        .permitAll()
                );

        logger.info("✅ Spring Security 설정이 완료되었습니다."); // 보안 설정 완료 로그

        return http.build();
    }
}