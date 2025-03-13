package com.auction.controller;

import com.auction.domain.AuctionItem;
import com.auction.domain.User;
import com.auction.repository.UserRepository;
import com.auction.service.AuctionItemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

/**
 * 마이페이지 컨트롤러
 */
@Controller
public class MyPageController {

    private final UserRepository userRepository;
    private final AuctionItemService auctionItemService;

    public MyPageController(UserRepository userRepository, AuctionItemService auctionItemService) {
        this.userRepository = userRepository;
        this.auctionItemService = auctionItemService;
    }

    /**
     * 마이페이지 조회
     * - 사용자 정보 표시 (이메일, 이름, 포인트)
     * - 사용자가 등록한 경매 상품 목록
     * - 사용자가 낙찰받은 상품 목록
     */
    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
            return "error";
        }

        User user = userOptional.get();
        model.addAttribute("user", user);

        // 사용자가 등록한 상품 (판매자로서)
        List<AuctionItem> myAuctionItems = auctionItemService.getAuctionItemsBySeller(user);
        model.addAttribute("myAuctionItems", myAuctionItems);

        // 사용자가 낙찰받은 상품 (구매자로서)
        List<AuctionItem> myWinningBids = auctionItemService.getAuctionItemsByWinner(user);
        model.addAttribute("myWinningBids", myWinningBids);

        return "mypage";
    }
}