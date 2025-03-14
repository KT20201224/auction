package com.auction.config;

import com.auction.domain.User;
import com.auction.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

/**
 * 데이터 초기화 클래스 - 애플리케이션 시작 시 기본 관리자 계정을 생성합니다.
 */
@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * DataInitializer 생성자
     *
     * @param userRepository 사용자 정보를 관리하는 리포지토리
     * @param passwordEncoder 비밀번호 암호화를 위한 인코더
     */
    public DataInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 애플리케이션 실행 시, 기본 관리자 계정을 자동으로 생성합니다.
     * 이미 존재하는 경우 추가로 생성되지 않습니다.
     */
    @PostConstruct
    public void init() {
        String adminEmail = "admin@auction.com";

        // 관리자 계정이 이미 존재하면 생성하지 않음
        if (userRepository.existsByEmail(adminEmail)) {
            logger.info("✅ 관리자 계정이 이미 존재합니다. (이메일: {})", adminEmail);
            return;
        }

        // ✅ 보안 주의: 기본 비밀번호를 코드에 직접 저장하는 것은 권장되지 않음
        User admin = new User();
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("admin123")); // 기본 비밀번호 설정
        admin.setName("관리자");
        admin.setPoints(0);
        admin.setAdmin(true); // ✅ 관리자 계정 설정

        userRepository.save(admin);
        logger.info("✅ 관리자 계정이 생성되었습니다! 이메일: {}, 비밀번호: {}", adminEmail, "admin123");
    }
}