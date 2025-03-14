package com.auction.controller;

import com.auction.domain.ChargeHistory;
import com.auction.domain.User;
import com.auction.repository.ChargeHistoryRepository;
import com.auction.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

/**
 * 포인트 충전 내역 컨트롤러
 */
@Controller
public class ChargeHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(ChargeHistoryController.class);

    private final UserRepository userRepository;
    private final ChargeHistoryRepository chargeHistoryRepository;

    /**
     * ChargeHistoryController 생성자
     *
     * @param userRepository 사용자 리포지토리
     * @param chargeHistoryRepository 포인트 충전 내역 리포지토리
     */
    public ChargeHistoryController(UserRepository userRepository, ChargeHistoryRepository chargeHistoryRepository) {
        this.userRepository = userRepository;
        this.chargeHistoryRepository = chargeHistoryRepository;
    }

    /**
     * 로그인한 사용자의 포인트 충전 내역 조회
     *
     * @param userDetails 로그인한 사용자 정보
     * @param model       템플릿에 전달할 모델 객체
     * @return 포인트 충전 내역 페이지 또는 로그인 페이지로 리디렉션
     */
    @GetMapping("/charge-history")
    public String chargeHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            logger.warn("❌ 포인트 충전 내역 조회 실패 - 로그인되지 않은 사용자");
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 이동
        }

        // ✅ 사용자 정보 조회
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
            logger.warn("❌ 포인트 충전 내역 조회 실패 - 사용자 정보를 찾을 수 없음 (이메일: {})", userDetails.getUsername());
            return "error";
        }

        User user = userOptional.get();
        List<ChargeHistory> chargeHistoryList = chargeHistoryRepository.findByUserOrderByChargedAtDesc(user);

        logger.info("✅ 포인트 충전 내역 조회 완료 - 사용자: {}, 조회된 내역 개수: {}", user.getEmail(), chargeHistoryList.size());

        model.addAttribute("chargeHistoryList", chargeHistoryList);
        return "charge-history";
    }
}