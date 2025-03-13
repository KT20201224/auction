package com.auction.controller;

import com.auction.domain.AuctionItem;
import com.auction.domain.User;
import com.auction.repository.AuctionItemRepository;
import com.auction.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

/**
 * 기본 홈 컨트롤러
 */
@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final AuctionItemRepository auctionItemRepository;

    public HomeController(UserRepository userRepository, AuctionItemRepository auctionItemRepository) {
        this.userRepository = userRepository;
        this.auctionItemRepository = auctionItemRepository;
    }

    /**
     * 메인 페이지 (최신 경매 상품 목록 포함)
     */
    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
            user.ifPresent(value -> model.addAttribute("userName", value.getName()));
        }

        // 최근 등록된 3개의 경매 상품 가져오기
        List<AuctionItem> latestItems = auctionItemRepository.findTop3ByOrderByEndTimeDesc();
        model.addAttribute("latestItems", latestItems);

        return "index";
    }

}