package com.auction.repository;

import com.auction.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 정보를 관리하는 레포지토리
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // 이메일로 사용자 조회
    boolean existsByEmail(String email); // 이메일 중복 체크
    List<User> findAllByOrderByIdAsc(); // 모든 사용자 조회 (관리자 페이지)

    List<User> findByIsBannedFalse(); // 정지되지 않은 사용자 조회
}