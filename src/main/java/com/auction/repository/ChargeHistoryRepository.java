package com.auction.repository;

import com.auction.domain.ChargeHistory;
import com.auction.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 🏷️ 포인트 충전 내역 리포지토리
 * - 사용자의 포인트 충전 기록을 관리하는 기능 제공
 */
public interface ChargeHistoryRepository extends JpaRepository<ChargeHistory, Long> {

    /**
     * 📌 특정 사용자의 포인트 충전 내역을 최근 충전순으로 조회
     *
     * @param user 사용자 (User 객체)
     * @return 사용자의 포인트 충전 내역 리스트 (최근 충전순 정렬)
     */
    List<ChargeHistory> findByUserOrderByChargedAtDesc(User user);
}