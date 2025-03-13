package com.auction.controller;

import com.auction.domain.AuctionItem;
import com.auction.domain.Bid;
import com.auction.domain.User;
import com.auction.repository.AuctionItemRepository;
import com.auction.repository.BidRepository;
import com.auction.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 마이페이지 컨트롤러 - 사용자의 등록 상품 및 낙찰 상품 조회
 */
@Controller
public class MyPageController {

    private final UserRepository userRepository;
    private final AuctionItemRepository auctionItemRepository;
    private final BidRepository bidRepository;

    public MyPageController(UserRepository userRepository, AuctionItemRepository auctionItemRepository, BidRepository bidRepository) {
        this.userRepository = userRepository;
        this.auctionItemRepository = auctionItemRepository;
        this.bidRepository = bidRepository;
    }

    /**
     * 마이페이지 조회
     */
    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 이동
        }

        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
            return "error"; // 사용자 정보가 없는 경우 오류 페이지로 이동
        }

        User user = userOptional.get();

        // 사용자가 등록한 경매 상품 조회
        List<AuctionItem> myAuctionItems = auctionItemRepository.findBySeller(user);

        // 사용자가 낙찰받은 경매 상품 조회
        List<AuctionItem> wonAuctionItems = auctionItemRepository.findByWinner(user);

        // 사용자가 입찰한 경매 상품 조회 (현재 진행 중인 경매만)
        List<Bid> userBids = bidRepository.findByBidder(user);
        List<AuctionItem> participatingAuctionItems = userBids.stream()
                .map(Bid::getAuctionItem)
                .filter(item -> item.getWinner() == null) // 낙찰되지 않은 상품만 필터링
                .distinct()
                .collect(Collectors.toList());

        // 각 상품의 현재 최고 입찰가 조회
        model.addAttribute("highestBids", getHighestBids(myAuctionItems, wonAuctionItems, participatingAuctionItems));

        model.addAttribute("email", user.getEmail());
        model.addAttribute("points", user.getPoints());
        model.addAttribute("myAuctionItems", myAuctionItems);
        model.addAttribute("wonAuctionItems", wonAuctionItems);
        model.addAttribute("participatingAuctionItems", participatingAuctionItems);

        return "mypage";
    }

    /**
     * 각 경매 상품의 최고 입찰가 조회
     */
    private Map<Long, Integer> getHighestBids(List<AuctionItem> myAuctionItems, List<AuctionItem> wonAuctionItems, List<AuctionItem> participatingAuctionItems) {
        Map<Long, Integer> highestBids = new HashMap<>();

        List<AuctionItem> allItems = new ArrayList<>();
        allItems.addAll(myAuctionItems);
        allItems.addAll(wonAuctionItems);
        allItems.addAll(participatingAuctionItems);

        for (AuctionItem item : allItems) {
            Bid highestBid = bidRepository.findTopByAuctionItemOrderByBidAmountDesc(item);
            highestBids.put(item.getId(), highestBid != null ? highestBid.getBidAmount() : item.getStartPrice());
        }

        return highestBids;
    }
}