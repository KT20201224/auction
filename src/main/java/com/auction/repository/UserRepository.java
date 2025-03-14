package com.auction.repository;

import com.auction.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 🏷️ 사용자 레포지토리
 * - 사용자 정보를 데이터베이스에서 관리하는 인터페이스
 * - Spring Data JPA를 활용하여 자동 구현
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 📌 이메일로 사용자 조회 (로그인 시 사용)
     *
     * @param email 사용자 이메일
     * @return 이메일이 일치하는 사용자 (Optional)
     */
    Optional<User> findByEmail(String email);

    /**
     * 📌 이메일 중복 여부 확인 (회원가입 시 사용)
     *
     * @param email 사용자 이메일
     * @return 이미 존재하는 이메일이면 true, 그렇지 않으면 false
     */
    boolean existsByEmail(String email);

    /**
     * 📌 모든 사용자 목록 조회 (관리자 페이지)
     * - 사용자 ID 기준으로 오름차순 정렬
     *
     * @return 모든 사용자 리스트
     */
    List<User> findAllByOrderByIdAsc();

    /**
     * 📌 정지되지 않은 사용자 목록 조회
     * - 관리자 페이지에서 활성 사용자 목록을 조회하는 용도로 사용 가능
     *
     * @return 정지되지 않은 사용자 리스트
     */
    List<User> findByIsBannedFalse();
}