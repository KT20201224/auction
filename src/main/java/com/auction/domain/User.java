package com.auction.domain;

import jakarta.persistence.*;
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
}