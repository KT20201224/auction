package com.auction.repository;

import com.auction.domain.AuctionItem;
import com.auction.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 입찰 리포지토리 (입찰 내역 저장 & 조회)
 */
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionItemOrderByBidAmountDesc(AuctionItem auctionItem);
    Bid findTopByAuctionItemOrderByBidAmountDesc(AuctionItem auctionItem);
}