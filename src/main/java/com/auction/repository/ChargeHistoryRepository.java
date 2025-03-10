package com.auction.repository;

import com.auction.domain.ChargeHistory;
import com.auction.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 포인트 충전 내역 리포지토리
 */
public interface ChargeHistoryRepository extends JpaRepository<ChargeHistory, Long> {
    List<ChargeHistory> findByUserOrderByChargedAtDesc(User user);
}