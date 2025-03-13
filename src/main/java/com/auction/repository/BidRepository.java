package com.auction.repository;

import com.auction.domain.AuctionItem;
import com.auction.domain.Bid;
import com.auction.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 입찰 리포지토리 (입찰 내역 저장 & 조회)
 */
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionItemOrderByBidAmountDesc(AuctionItem auctionItem);
    Bid findTopByAuctionItemOrderByBidAmountDesc(AuctionItem auctionItem);

    List<Bid> findByBidder(User bidder); // ✅ 특정 사용자가 입찰한 상품 조회 (추가)
    /**
     * 특정 경매 상품의 모든 입찰 내역 삭제
     */
    void deleteByAuctionItem(AuctionItem auctionItem);
}