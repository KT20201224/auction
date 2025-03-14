package com.auction.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 포인트 충전 내역 엔티티
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "charge_history")
public class ChargeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 충전한 사용자

    @Column(nullable = false)
    private int amount; // 충전 금액

    @Column(nullable = false)
    private LocalDateTime chargedAt; // 충전 날짜 및 시간

    /**
     * 🔹 포인트 충전 내역을 생성하는 정적 팩토리 메서드
     * @param user 충전한 사용자
     * @param amount 충전 금액
     * @return 새로운 ChargeHistory 객체
     */
    public static ChargeHistory createChargeHistory(User user, int amount) {
        return ChargeHistory.builder()
                .user(user)
                .amount(amount)
                .chargedAt(LocalDateTime.now())
                .build();
    }
}