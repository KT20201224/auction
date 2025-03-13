package com.auction.repository;

import com.auction.domain.AuctionItem;
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
}