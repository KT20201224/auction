package com.auction.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 경매 상품 엔티티
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "auction_item")
public class AuctionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 ID (자동 증가)

    @Column(nullable = false)
    private String name; // 상품명

    @Column(nullable = false, length = 500)
    private String description; // 상품 설명

    @Column(nullable = false)
    private int startPrice; // 시작 가격

    @Column(nullable = false)
    private LocalDateTime endTime; // 경매 마감 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User seller; // 상품을 등록한 사용자
}