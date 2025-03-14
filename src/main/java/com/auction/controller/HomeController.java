package com.auction.controller;

import com.auction.domain.AuctionItem;
import com.auction.domain.User;
import com.auction.repository.AuctionItemRepository;
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
 * 홈 컨트롤러 - 메인 페이지 관리
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final UserRepository userRepository;
    private final AuctionItemRepository auctionItemRepository;

    /**
     * HomeController 생성자
     *
     * @param userRepository       사용자 리포지토리
     * @param auctionItemRepository 경매 상품 리포지토리
     */
    public HomeController(UserRepository userRepository, AuctionItemRepository auctionItemRepository) {
        this.userRepository = userRepository;
        this.auctionItemRepository = auctionItemRepository;
    }

    /**
     * 메인 페이지 (최신 경매 상품 목록 포함)
     *
     * @param userDetails 로그인한 사용자 정보 (없을 경우 null)
     * @param model       템플릿에 전달할 모델 객체
     * @return 메인 페이지 (index.html)
     */
    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // ✅ 로그인한 사용자의 정보 가져오기
        if (userDetails != null) {
            userRepository.findByEmail(userDetails.getUsername()).ifPresent(user -> {
                model.addAttribute("userName", user.getName());
                model.addAttribute("isAdmin", user.isAdmin()); // ✅ 관리자 여부 전달
                logger.info("✅ 홈 페이지 접근 - 사용자: {}, 관리자 여부: {}", user.getEmail(), user.isAdmin());
            });
        } else {
            logger.info("✅ 홈 페이지 접근 - 비로그인 사용자");
        }

        // ✅ 최신 3개의 경매 상품 조회
        List<AuctionItem> latestItems = auctionItemRepository.findTop3ByOrderByEndTimeDesc();
        model.addAttribute("latestItems", latestItems);
        logger.info("✅ 최근 경매 상품 {}개 조회", latestItems.size());

        return "index";
    }
}