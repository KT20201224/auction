package com.auction.config;

import com.auction.domain.User;
import com.auction.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

/**
 * 초기 관리자 계정 생성
 */
@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (userRepository.existsByEmail("admin@auction.com")) {
            return; // 이미 존재하면 생성하지 않음
        }

        User admin = new User();
        admin.setEmail("admin@auction.com");
        admin.setPassword(passwordEncoder.encode("admin123")); // 기본 비밀번호
        admin.setName("관리자");
        admin.setPoints(0);
        admin.setAdmin(true); // ✅ 관리자 계정 설정

        userRepository.save(admin);
        System.out.println("✅ 관리자 계정이 생성되었습니다! 이메일: admin@auction.com, 비밀번호: admin123");
    }
}