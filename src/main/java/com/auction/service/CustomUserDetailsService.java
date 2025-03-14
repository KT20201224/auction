package com.auction.service;

import com.auction.domain.User;
import com.auction.repository.UserRepository;
import com.auction.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 사용자 정보를 가져오는 서비스 클래스
 * Spring Security의 UserDetailsService를 구현하여
 * 데이터베이스에서 사용자 정보를 조회하는 역할을 수행함.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 생성자 - UserRepository 주입
     *
     * @param userRepository 사용자 저장소 인터페이스
     */
    public CustomUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 이메일을 기반으로 사용자 정보를 조회하여 UserDetails 객체를 반환
     *
     * @param email 사용자 이메일
     * @return UserDetails 사용자 정보
     * @throws UsernameNotFoundException 사용자가 존재하지 않는 경우 예외 발생
     */
    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> {
                    System.out.println("🚨 [ERROR] 사용자 이메일을 찾을 수 없음: " + email);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
                });
    }
}