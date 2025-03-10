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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * 입찰 컨트롤러
 */
@Controller
public class BidController {

    private final BidRepository bidRepository;
    private final AuctionItemRepository auctionItemRepository;
    private final UserRepository userRepository;

    public BidController(BidRepository bidRepository, AuctionItemRepository auctionItemRepository, UserRepository userRepository) {
        this.bidRepository = bidRepository;
        this.auctionItemRepository = auctionItemRepository;
        this.userRepository = userRepository;
    }

    /**
     * 입찰 처리
     */
    @PostMapping("/bid")
    public String placeBid(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam("auctionItemId") Long auctionItemId,
                           @RequestParam("bidAmount") int bidAmount,
                           Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
        Optional<AuctionItem> auctionItemOptional = auctionItemRepository.findById(auctionItemId);

        if (userOptional.isEmpty() || auctionItemOptional.isEmpty()) {
            model.addAttribute("errorMessage", "사용자 또는 경매 상품을 찾을 수 없습니다.");
            return "error";
        }

        User bidder = userOptional.get();
        AuctionItem auctionItem = auctionItemOptional.get();

        // 현재 최고 입찰가 확인
        Bid highestBid = bidRepository.findTopByAuctionItemOrderByBidAmountDesc(auctionItem);
        int currentHighestBid = (highestBid != null) ? highestBid.getBidAmount() : auctionItem.getStartPrice();

        if (bidAmount <= currentHighestBid) {
            model.addAttribute("errorMessage", "입찰 금액은 현재 최고 입찰가보다 높아야 합니다.");
            return "error";
        }

        // 새로운 입찰 저장
        Bid newBid = new Bid(bidder, auctionItem, bidAmount);
        bidRepository.save(newBid);

        return "redirect:/auction-item/" + auctionItemId; // 입찰 후 상품 상세 페이지로 이동
    }
}