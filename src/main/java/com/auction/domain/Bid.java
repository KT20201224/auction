package com.auction.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 입찰 엔티티 (입찰 내역 저장)
 */
@Entity
@Getter
@Setter
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
    private LocalDateTime bidTime; // 입찰 시간

    public Bid(User bidder, AuctionItem auctionItem, int bidAmount) {
        this.bidder = bidder;
        this.auctionItem = auctionItem;
        this.bidAmount = bidAmount;
        this.bidTime = LocalDateTime.now();
    }
}