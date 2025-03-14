package com.auction.repository;

import com.auction.domain.AuctionItem;
import com.auction.domain.Bid;
import com.auction.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 🏷️ 입찰 리포지토리
 * - 입찰 내역을 저장 및 조회하는 기능 제공
 */
public interface BidRepository extends JpaRepository<Bid, Long> {

    /**
     * 📌 특정 경매 상품의 입찰 내역을 높은 금액 순으로 조회
     *
     * @param auctionItem 경매 상품
     * @return 입찰 내역 리스트 (높은 금액순 정렬)
     */
    List<Bid> findByAuctionItemOrderByBidAmountDesc(AuctionItem auctionItem);

    /**
     * 📌 특정 경매 상품에서 가장 높은 입찰가를 기록한 입찰 조회
     *
     * @param auctionItem 경매 상품
     * @return 가장 높은 입찰 (없을 경우 null 반환)
     */
    Bid findTopByAuctionItemOrderByBidAmountDesc(AuctionItem auctionItem);

    /**
     * 📌 특정 사용자가 입찰한 모든 입찰 내역 조회
     *
     * @param bidder 입찰자 (User 객체)
     * @return 사용자의 모든 입찰 내역
     */
    List<Bid> findByBidder(User bidder);

    /**
     * 📌 특정 경매 상품의 모든 입찰 내역 삭제 (상품 삭제 시 활용)
     *
     * @param auctionItem 삭제할 경매 상품
     */
    void deleteByAuctionItem(AuctionItem auctionItem);
}