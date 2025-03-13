package com.auction.repository;

import com.auction.domain.AuctionItem;
import com.auction.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 경매 상품 리포지토리
 */
public interface AuctionItemRepository extends JpaRepository<AuctionItem, Long> {
    List<AuctionItem> findAllByOrderByEndTimeAsc(); // 마감 시간 기준으로 상품 정렬
    List<AuctionItem> findByEndTimeBeforeAndWinnerIsNull(LocalDateTime now);
    List<AuctionItem> findByWinnerNotNullOrderByEndTimeDesc();
    List<AuctionItem> findTop3ByOrderByEndTimeDesc(); // 최신 경매 상품 3개 조회

    /**
     * 특정 판매자가 등록한 경매 상품 목록 조회
     */
    List<AuctionItem> findBySeller(User seller);

    /**
     * 특정 사용자가 낙찰받은 상품 목록 조회
     */
    List<AuctionItem> findByWinner(User winner);

}