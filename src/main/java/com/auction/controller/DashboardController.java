package com.auction.controller;

import com.auction.domain.User;
import com.auction.repository.UserRepository;
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

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 대시보드 페이지
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
            if (user.isPresent()) {
                model.addAttribute("userName", user.get().getName());
                model.addAttribute("email", user.get().getEmail());
                model.addAttribute("points", user.get().getPoints());
            }
        }
        return "dashboard";
    }
}