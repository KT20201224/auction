package com.auction.controller;

import com.auction.domain.AuctionItem;
import com.auction.domain.Bid;
import com.auction.domain.User;
import com.auction.repository.AuctionItemRepository;
import com.auction.repository.BidRepository;
import com.auction.repository.UserRepository;
import com.auction.service.AuctionItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 경매 상품 컨트롤러
 */
@Controller
public class AuctionItemController {

    private static final Logger logger = LoggerFactory.getLogger(AuctionItemController.class);

    private final AuctionItemRepository auctionItemRepository;
    private final AuctionItemService auctionItemService;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    /**
     * AuctionItemController 생성자
     *
     * @param auctionItemRepository 경매 상품 리포지토리
     * @param userRepository 사용자 리포지토리
     * @param bidRepository 입찰 리포지토리
     * @param auctionItemService 경매 상품 서비스
     */
    public AuctionItemController(AuctionItemRepository auctionItemRepository, UserRepository userRepository,
                                 BidRepository bidRepository, AuctionItemService auctionItemService) {
        this.auctionItemRepository = auctionItemRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
        this.auctionItemService = auctionItemService;
    }

    /**
     * 경매 상품 목록 페이지 (검색 기능 포함)
     *
     * @param search 검색어 (선택 사항)
     * @param model  템플릿에 전달할 모델 객체
     * @return 경매 상품 목록 페이지
     */
    @GetMapping("/auction-items")
    public String auctionItems(@RequestParam(value = "search", required = false) String search, Model model) {
        List<AuctionItem> items = (search != null && !search.trim().isEmpty())
                ? auctionItemRepository.findByNameContainingIgnoreCaseOrderByEndTimeAsc(search)
                : auctionItemRepository.findAllByOrderByEndTimeAsc();

        // ✅ 검색어가 있을 경우 로깅
        if (search != null && !search.trim().isEmpty()) {
            logger.info("🔍 검색 실행 - 키워드: {}", search);
            model.addAttribute("searchKeyword", search);
        }

        // ✅ 각 경매 상품의 최고 입찰가 조회
        Map<Long, Integer> highestBids = new HashMap<>();
        for (AuctionItem item : items) {
            Bid highestBid = bidRepository.findTopByAuctionItemOrderByBidAmountDesc(item);
            highestBids.put(item.getId(), (highestBid != null) ? highestBid.getBidAmount() : item.getStartPrice());
        }

        model.addAttribute("items", items);
        model.addAttribute("highestBids", highestBids);
        return "auction-items";
    }

    /**
     * 경매 상품 등록 페이지 이동
     *
     * @return 경매 상품 등록 페이지 (auction-item-form.html)
     */
    @GetMapping("/auction-item/new")
    public String auctionItemForm() {
        return "auction-item-form";
    }

    /**
     * 경매 상품 등록 처리
     *
     * @param userDetails 로그인한 사용자 정보
     * @param name        상품명
     * @param description 상품 설명
     * @param startPrice  시작 가격
     * @param endTime     마감 시간 (String -> LocalDateTime 변환)
     * @return 경매 상품 목록 페이지로 리디렉션
     */
    @PostMapping("/auction-item/new")
    public String createAuctionItem(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestParam("name") String name,
                                    @RequestParam("description") String description,
                                    @RequestParam("startPrice") int startPrice,
                                    @RequestParam("endTime") String endTime) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            logger.warn("🚨 사용자 정보 없음 - 상품 등록 실패");
            return "error";
        }

        User seller = userOptional.get();
        AuctionItem item = AuctionItem.builder()
                .name(name)
                .description(description)
                .startPrice(startPrice)
                .endTime(LocalDateTime.parse(endTime))
                .seller(seller)
                .build();
        auctionItemRepository.save(item);

        logger.info("📦 상품 등록 완료 - 상품명: {}, 등록자: {}", name, seller.getEmail());
        return "redirect:/auction-items";
    }

    /**
     * 특정 경매 상품 상세 조회
     *
     * @param id         경매 상품 ID
     * @param model      템플릿에 전달할 모델 객체
     * @param userDetails 로그인한 사용자 정보
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

        // ✅ 최고 입찰가 조회
        Bid highestBid = bidRepository.findTopByAuctionItemOrderByBidAmountDesc(item);
        int highestBidAmount = (highestBid != null) ? highestBid.getBidAmount() : item.getStartPrice();
        model.addAttribute("highestBidAmount", highestBidAmount);

        // ✅ 로그인한 사용자 정보 추가 (보유 포인트 포함)
        if (userDetails != null) {
            Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("user", user);
                model.addAttribute("userPoints", user.getPoints());
            }
        }

        return "auction-item-detail";
    }

    /**
     * 경매 상품 구매 확정 요청 처리
     *
     * @param id         경매 상품 ID
     * @param userDetails 로그인한 사용자 정보
     * @param model      템플릿에 전달할 모델 객체
     * @return 상품 상세 페이지로 리디렉션 또는 오류 페이지
     */
    @PostMapping("/auction-item/{id}/confirm-purchase")
    public String confirmPurchase(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
            return "error";
        }

        User buyer = userOptional.get();
        try {
            auctionItemService.confirmPurchase(id, buyer);
            logger.info("✅ 구매 확정 완료 - 상품 ID: {}, 구매자: {}", id, buyer.getEmail());
            return "redirect:/auction-item/" + id;
        } catch (IllegalStateException | IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            logger.warn("⚠ 구매 확정 실패 - 상품 ID: {}, 오류: {}", id, e.getMessage());
            return "error";
        }
    }
}