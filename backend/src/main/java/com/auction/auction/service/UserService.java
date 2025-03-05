package com.auction.auction.service;

import com.auction.auction.entity.User;
import com.auction.auction.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * UserService는 회원가입, 로그인 등의 비즈니스 로직을 처리하는 클래스.
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
     * 이메일을 기반으로 사용자 정보를 조회하는 메서드.
     * @param email 사용자 이메일
     * @return Optional<User> (사용자가 존재하면 반환)
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 회원가입 기능: 새로운 유저를 생성하고 비밀번호를 암호화하여 저장함.
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @param nickname 사용자 닉네임
     * @return 저장된 User 객체
     */
    public User registerUser(String email, String password, String nickname) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // 비밀번호 암호화
        user.setNickname(nickname);

        return userRepository.save(user);  // DB에 저장
    }
}