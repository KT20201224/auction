package com.auction.controller;

import com.auction.domain.ChargeHistory;
import com.auction.domain.User;
import com.auction.repository.ChargeHistoryRepository;
import com.auction.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final ChargeHistoryRepository chargeHistoryRepository;

    public ChargeHistoryController(UserRepository userRepository, ChargeHistoryRepository chargeHistoryRepository) {
        this.userRepository = userRepository;
        this.chargeHistoryRepository = chargeHistoryRepository;
    }

    /**
     * 로그인한 사용자의 포인트 충전 내역 조회
     */
    @GetMapping("/charge-history")
    public String chargeHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 이동
        }

        // 사용자 정보 조회
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
            return "error"; // 사용자 정보가 없는 경우 오류 페이지로 이동
        }

        User user = userOptional.get();
        List<ChargeHistory> chargeHistoryList = chargeHistoryRepository.findByUserOrderByChargedAtDesc(user);

        // 🚀 디버깅 로그: 충전 내역 확인
        System.out.println("조회된 충전 내역 개수: " + chargeHistoryList.size());

        model.addAttribute("chargeHistoryList", chargeHistoryList);
        return "charge-history";
    }
}