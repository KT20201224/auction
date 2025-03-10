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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 포인트 충전 컨트롤러
 */
@Controller
public class PointChargeController {

    private final UserRepository userRepository;
    private final ChargeHistoryRepository chargeHistoryRepository;

    public PointChargeController(UserRepository userRepository, ChargeHistoryRepository chargeHistoryRepository) {
        this.userRepository = userRepository;
        this.chargeHistoryRepository = chargeHistoryRepository;
    }

    /**
     * 포인트 충전 페이지
     */
    @GetMapping("/charge")
    public String chargePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
            user.ifPresent(value -> model.addAttribute("points", value.getPoints()));
        }
        return "charge";
    }

    /**
     * 포인트 충전 요청 처리
     */
    @PostMapping("/charge")
    @Transactional
    public String chargePoints(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam("amount") int amount, Model model) {
        if (userDetails != null) {
            Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setPoints(user.getPoints() + amount); // 포인트 추가
                userRepository.save(user);

                // 충전 내역 저장
                ChargeHistory chargeHistory = new ChargeHistory(user, amount);
                chargeHistoryRepository.save(chargeHistory);

                model.addAttribute("message", "포인트 충전이 완료되었습니다!");
                model.addAttribute("points", user.getPoints());
            }
        }
        return "charge";
    }
}