package com.auction.service;

import com.auction.domain.User;
import com.auction.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스 - 회원가입 및 사용자 관리 로직 처리
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 생성자 - UserRepository 및 BCryptPasswordEncoder 주입
     *
     * @param userRepository 사용자 리포지토리
     * @param passwordEncoder 비밀번호 암호화 도구
     */
    public UserService(final UserRepository userRepository, final BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입 기능
     *
     * @param email    사용자 이메일
     * @param password 비밀번호 (암호화됨)
     * @param name     사용자 이름
     * @throws IllegalArgumentException 이미 사용 중인 이메일일 경우 예외 발생
     */
    @Transactional
    public void registerUser(final String email, final String password, final String name) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화 후 사용자 생성
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setPoints(0);       // 기본 포인트 설정
        user.setAdmin(false);     // 일반 사용자 기본값
        user.setBanned(false);    // 정지 여부 기본값

        // 사용자 저장
        userRepository.save(user);
    }
}