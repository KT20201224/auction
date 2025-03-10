package com.auction.service;

import com.auction.domain.User;
import com.auction.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스 - 회원가입 로직 처리
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입 기능
     * @param email 사용자 이메일
     * @param password 비밀번호 (암호화됨)
     * @param name 사용자 이름
     */
    @Transactional
    public void registerUser(String email, String password, String name) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 저장
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setName(name);
        userRepository.save(user);
    }
}