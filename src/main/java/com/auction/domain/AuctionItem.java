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
@Table(name = "auction_item") // 기존 테이블 유지
public class AuctionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 ID

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
    private User seller; // 판매자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner; // 낙찰자 (없으면 유찰)

    @Column(nullable = false)
    private boolean isPurchased = false; // 🔹 구매 확정 여부 (기본값 false)

    /**
     * 경매가 마감되었는지 여부를 반환
     *
     * @return true(마감됨), false(진행 중)
     */
    public boolean isAuctionEnded() {
        return LocalDateTime.now().isAfter(this.endTime);
    }

    /**
     * 구매 확정 여부를 반환
     *
     * @return true(구매 완료), false(미구매)
     */
    public boolean isPurchased() {
        return isPurchased;
    }

    /**
     * 낙찰자가 구매 확정을 하면 상태를 변경
     */
    public void confirmPurchase() {
        this.isPurchased = true;
    }
}