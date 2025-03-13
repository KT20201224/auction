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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * 입찰 컨트롤러
 */
@Controller
public class BidController {

    private final BidRepository bidRepository;
    private final AuctionItemRepository auctionItemRepository;
    private final UserRepository userRepository;

    /**
     * BidController 생성자
     *
     * @param bidRepository 입찰 리포지토리
     * @param auctionItemRepository 경매 상품 리포지토리
     * @param userRepository 사용자 리포지토리
     */
    public BidController(BidRepository bidRepository, AuctionItemRepository auctionItemRepository, UserRepository userRepository) {
        this.bidRepository = bidRepository;
        this.auctionItemRepository = auctionItemRepository;
        this.userRepository = userRepository;
    }

    /**
     * 특정 경매 상품의 입찰 내역 조회
     *
     * @param id 경매 상품 ID
     * @param model 템플릿에 전달할 모델 객체
     * @return 입찰 내역 페이지 (bid-list.html) 또는 오류 페이지
     */
    @GetMapping("/auction-item/{id}/bids")
    public String viewBids(@PathVariable Long id, Model model) {
        Optional<AuctionItem> auctionItemOptional = auctionItemRepository.findById(id);
        if (auctionItemOptional.isPresent()) {
            AuctionItem auctionItem = auctionItemOptional.get();
            List<Bid> bidList = bidRepository.findByAuctionItemOrderByBidAmountDesc(auctionItem);
            model.addAttribute("auctionItem", auctionItem);
            model.addAttribute("bidList", bidList);
            return "bid-list";
        } else {
            model.addAttribute("errorMessage", "해당 상품을 찾을 수 없습니다.");
            return "error";
        }
    }

    /**
     * 입찰 처리 (포인트 차감 및 기존 최고 입찰자 포인트 환불)
     *
     * @param userDetails 로그인한 사용자 정보
     * @param auctionItemId 경매 상품 ID
     * @param bidAmount 사용자가 입력한 입찰 금액
     * @param model 템플릿에 전달할 모델 객체
     * @return 상품 상세 페이지로 리디렉션 또는 오류 페이지
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

        if (bidder.getPoints() < bidAmount) {
            model.addAttribute("errorMessage", "보유 포인트가 부족합니다.");
            return "error";
        }

        // 이전 최고 입찰자의 포인트 환불
        if (highestBid != null) {
            User previousBidder = highestBid.getBidder();
            previousBidder.setPoints(previousBidder.getPoints() + highestBid.getBidAmount());
            userRepository.save(previousBidder);
        }

        // 새로운 입찰 진행 (포인트 차감)
        bidder.setPoints(bidder.getPoints() - bidAmount);
        userRepository.save(bidder);

        Bid newBid = new Bid(bidder, auctionItem, bidAmount);
        bidRepository.save(newBid);

        return "redirect:/auction-item/" + auctionItemId;
    }
}