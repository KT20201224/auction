package com.auction.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 엔티티 클래스
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users") // 테이블 이름 설정
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 ID
    private Long id;

    @Column(nullable = false, unique = true) // 이메일 중복 방지
    private String email;

    @Column(nullable = false)
    private String password; // 암호화된 비밀번호 저장

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int points = 0; // 기본 포인트 (초기값 0)

    @Column(nullable = false)
    private boolean isAdmin = false; // 관리자 여부 (기본값: 일반 사용자)

    @Column(nullable = false)
    private boolean isBanned = false; // 정지된 사용자 여부 (기본값: false)

    /**
     * 🔹 생성자 (회원가입 시 사용)
     */
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.points = 0;
        this.isAdmin = false;
        this.isBanned = false;
    }

    /**
     * 🔹 포인트 추가
     */
    public void addPoints(int amount) {
        if (amount > 0) {
            this.points += amount;
        }
    }

    /**
     * 🔹 포인트 차감
     */
    public void subtractPoints(int amount) {
        if (amount > 0 && this.points >= amount) {
            this.points -= amount;
        } else {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
    }

    /**
     * 🔹 관리자 권한 부여
     */
    public void grantAdmin() {
        this.isAdmin = true;
    }

    /**
     * 🔹 사용자 정지
     */
    public void banUser() {
        this.isBanned = true;
    }

    /**
     * 🔹 사용자 정지 해제
     */
    public void unbanUser() {
        this.isBanned = false;
    }
}