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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 경매 상품 컨트롤러
 */
@Controller
public class AuctionItemController {

    private final AuctionItemRepository auctionItemRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    public AuctionItemController(AuctionItemRepository auctionItemRepository, UserRepository userRepository, BidRepository bidRepository) {
        this.auctionItemRepository = auctionItemRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    /**
     * 경매 상품 목록 페이지
     * @param model 템플릿에 전달할 모델 객체
     * @return 경매 상품 목록 페이지 (auction-items.html)
     */
    @GetMapping("/auction-items")
    public String auctionItems(Model model) {
        List<AuctionItem> items = auctionItemRepository.findAllByOrderByEndTimeAsc();
        model.addAttribute("items", items);
        return "auction-items";
    }

    /**
     * 경매 상품 등록 페이지
     * @return 상품 등록 페이지 (auction-item-form.html)
     */
    @GetMapping("/auction-item/new")
    public String auctionItemForm() {
        return "auction-item-form";
    }

    /**
     * 경매 상품 등록 처리
     * @param userDetails 로그인한 사용자 정보
     * @param name 상품명
     * @param description 상품 설명
     * @param startPrice 시작 가격
     * @param endTime 마감 시간 (String -> LocalDateTime 변환)
     * @return 경매 상품 목록으로 리디렉션
     */
    @PostMapping("/auction-item/new")
    public String createAuctionItem(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestParam("name") String name,
                                    @RequestParam("description") String description,
                                    @RequestParam("startPrice") int startPrice,
                                    @RequestParam("endTime") String endTime) {
        if (userDetails != null) {
            Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
            if (userOptional.isPresent()) {
                User seller = userOptional.get();
                AuctionItem item = AuctionItem.builder()
                        .name(name)
                        .description(description)
                        .startPrice(startPrice)
                        .endTime(LocalDateTime.parse(endTime))
                        .seller(seller)
                        .build();
                auctionItemRepository.save(item);
            }
        }
        return "redirect:/auction-items"; // 상품 등록 후 경매 상품 목록으로 이동
    }

    /**
     * 특정 경매 상품 상세 조회
     *
     * @param id 경매 상품 ID
     * @param model 템플릿에 전달할 모델 객체
     * @return 경매 상품 상세 페이지
     */
    @GetMapping("/auction-item/{id}")
    public String auctionItemDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<AuctionItem> itemOptional = auctionItemRepository.findById(id);
        if (itemOptional.isEmpty()) {
            model.addAttribute("errorMessage", "해당 상품을 찾을 수 없습니다.");
            return "error";
        }

        AuctionItem item = itemOptional.get();
        model.addAttribute("item", item);

        // 최고 입찰가 조회
        Bid highestBid = bidRepository.findTopByAuctionItemOrderByBidAmountDesc(item);
        int highestBidAmount = (highestBid != null) ? highestBid.getBidAmount() : item.getStartPrice();
        model.addAttribute("highestBidAmount", highestBidAmount);

        // 로그인한 사용자 정보 추가
        if (userDetails != null) {
            Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
            userOptional.ifPresent(user -> model.addAttribute("user", user));
        }

        return "auction-item-detail";
    }
}