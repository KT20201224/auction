package com.auction.controller;

import com.auction.domain.User;
import com.auction.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

/**
 * 대시보드 컨트롤러 - 로그인한 사용자의 정보를 표시
 */
@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final UserRepository userRepository;

    /**
     * DashboardController 생성자
     *
     * @param userRepository 사용자 리포지토리
     */
    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 로그인한 사용자의 대시보드 페이지
     *
     * @param userDetails 로그인한 사용자 정보
     * @param model       템플릿에 전달할 모델 객체
     * @return 대시보드 페이지 또는 로그인 페이지로 리디렉션
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            logger.warn("❌ 대시보드 접근 실패 - 로그인되지 않은 사용자");
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 이동
        }

        // ✅ 사용자 정보 조회
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("userName", user.getName());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("points", user.getPoints());

            logger.info("✅ 대시보드 접근 - 사용자: {}, 포인트: {}", user.getEmail(), user.getPoints());
        } else {
            logger.warn("❌ 대시보드 접근 실패 - 사용자 정보를 찾을 수 없음 (이메일: {})", userDetails.getUsername());
            model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
            return "error"; // 사용자 정보가 없는 경우 오류 페이지로 이동
        }

        return "dashboard";
    }
}