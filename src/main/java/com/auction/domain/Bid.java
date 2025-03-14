package com.auction.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 입찰 엔티티 (입찰 내역 저장)
 */
@Entity
@Getter
@Setter // ✅ Setter 허용 (서비스 계층에서 값 변경 가능)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 입찰 ID (자동 증가)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User bidder; // 입찰한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_item_id", nullable = false)
    private AuctionItem auctionItem; // 입찰한 경매 상품

    @Column(nullable = false)
    private int bidAmount; // 입찰 금액

    @Column(nullable = false)
    private LocalDateTime bidTime; // 입찰 시간 (자동 설정)

    /**
     * 새로운 입찰 객체를 생성하는 정적 메서드
     *
     * @param bidder 입찰자
     * @param auctionItem 경매 상품
     * @param bidAmount 입찰 금액
     * @return 생성된 입찰 객체
     */
    public static Bid createBid(User bidder, AuctionItem auctionItem, int bidAmount) {
        return Bid.builder()
                .bidder(bidder)
                .auctionItem(auctionItem)
                .bidAmount(bidAmount)
                .bidTime(LocalDateTime.now())
                .build();
    }
}