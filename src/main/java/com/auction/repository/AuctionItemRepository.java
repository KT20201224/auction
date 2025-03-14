package com.auction.repository;

import com.auction.domain.AuctionItem;
import com.auction.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 🏷️ 경매 상품 리포지토리
 * - 경매 상품 데이터 조회 및 검색 기능을 제공
 */
public interface AuctionItemRepository extends JpaRepository<AuctionItem, Long> {

    /**
     * 📌 마감 시간이 임박한 순으로 모든 경매 상품 조회
     */
    List<AuctionItem> findAllByOrderByEndTimeAsc();

    /**
     * 📌 마감 시간이 지나고 아직 낙찰자가 없는 경매 상품 조회 (유찰된 상품)
     */
    List<AuctionItem> findByEndTimeBeforeAndWinnerIsNull(LocalDateTime now);

    /**
     * 📌 낙찰된 상품 목록을 마감 시간 순으로 정렬하여 조회
     */
    List<AuctionItem> findByWinnerNotNullOrderByEndTimeDesc();

    /**
     * 📌 최신 경매 상품 3개 조회 (홈페이지에서 활용)
     */
    List<AuctionItem> findTop3ByOrderByEndTimeDesc();

    /**
     * 📌 특정 사용자가 판매자로 등록한 경매 상품 조회
     *
     * @param seller 판매자 (User 객체)
     * @return 판매자가 등록한 경매 상품 목록
     */
    List<AuctionItem> findBySeller(User seller);

    /**
     * 📌 특정 사용자가 낙찰받은 상품 조회
     *
     * @param winner 낙찰자 (User 객체)
     * @return 사용자가 낙찰받은 상품 목록
     */
    List<AuctionItem> findByWinner(User winner);

    /**
     * 📌 상품명을 기준으로 검색 (대소문자 구분 없이 포함된 문자열 검색)
     * - 검색 결과는 마감 시간이 임박한 순서대로 정렬됨
     *
     * @param name 검색어
     * @return 검색 결과에 해당하는 경매 상품 목록
     */
    List<AuctionItem> findByNameContainingIgnoreCaseOrderByEndTimeAsc(String name);
}