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

    // 새로운 충전 내역을 생성하는 생성자 추가
    public ChargeHistory(User user, int amount) {
        this.user = user;
        this.amount = amount;
        this.chargedAt = LocalDateTime.now();
    }
}