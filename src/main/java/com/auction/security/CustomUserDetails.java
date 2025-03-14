package com.auction.security;

import com.auction.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 🛡️ Spring Security 사용자 정보 클래스
 * - User 엔티티를 기반으로 인증 및 권한을 관리
 * - `UserDetails` 인터페이스를 구현하여 Spring Security와 연동
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * 🔹 생성자: `User` 객체를 받아 `CustomUserDetails` 초기화
     *
     * @param user 인증할 사용자 정보
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * 🔹 사용자 권한 반환
     * - 관리자는 `ADMIN` 권한을 가짐
     * - 일반 사용자는 권한 없음
     *
     * @return 사용자의 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.isAdmin()
                ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                : Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * 🔹 사용자 비밀번호 반환
     *
     * @return 암호화된 비밀번호
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 🔹 사용자 이름 (이메일) 반환
     *
     * @return 사용자 이메일 (로그인 ID)
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * 🔹 계정 만료 여부 확인
     * - `true`: 계정 활성
     *
     * @return 계정이 만료되지 않았는지 여부
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 🔹 계정 잠김 여부 확인
     * - `true`: 계정이 잠기지 않음
     *
     * @return 계정이 잠기지 않았는지 여부
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 🔹 비밀번호 만료 여부 확인
     * - `true`: 비밀번호가 만료되지 않음
     *
     * @return 비밀번호가 만료되지 않았는지 여부
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 🔹 계정 활성 여부 확인
     * - `true`: 계정 활성화됨
     * - `false`: 계정이 정지된 경우
     *
     * @return 계정이 활성 상태인지 여부
     */
    @Override
    public boolean isEnabled() {
        return !user.isBanned();
    }
}