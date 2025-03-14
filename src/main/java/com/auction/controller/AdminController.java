package com.auction.controller;

import com.auction.domain.AuctionItem;
import com.auction.domain.User;
import com.auction.repository.AuctionItemRepository;
import com.auction.repository.BidRepository;
import com.auction.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * 관리자 컨트롤러 - 회원 및 경매 상품을 관리하는 기능 제공
 */
@Controller
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserRepository userRepository;
    private final AuctionItemRepository auctionItemRepository;
    private final BidRepository bidRepository;

    /**
     * AdminController 생성자
     *
     * @param userRepository 사용자 레포지토리
     * @param auctionItemRepository 경매 상품 레포지토리
     * @param bidRepository 입찰 레포지토리
     */
    public AdminController(UserRepository userRepository, AuctionItemRepository auctionItemRepository, BidRepository bidRepository) {
        this.userRepository = userRepository;
        this.auctionItemRepository = auctionItemRepository;
        this.bidRepository = bidRepository;
    }

    /**
     * 관리자 페이지 - 모든 회원 및 경매 상품 조회
     *
     * @param userDetails 로그인한 관리자 정보
     * @param model       템플릿에 전달할 데이터
     * @return 관리자 페이지 (admin.html)
     */
    @GetMapping("/admin")
    public String adminPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> adminUser = userRepository.findByEmail(userDetails.getUsername());

        // 🔹 현재 로그인한 사용자가 관리자인지 확인
        if (adminUser.isEmpty() || !adminUser.get().isAdmin()) {
            logger.warn("🚨 비인가 사용자가 관리자 페이지에 접근을 시도했습니다: {}", userDetails.getUsername());
            return "error"; // 관리자가 아니라면 접근 차단
        }

        // 🔹 모든 사용자 및 경매 상품 목록 조회
        List<User> users = userRepository.findAllByOrderByIdAsc();
        List<AuctionItem> items = auctionItemRepository.findAllByOrderByEndTimeAsc();

        model.addAttribute("users", users);
        model.addAttribute("items", items);

        logger.info("✅ 관리자 페이지 로드 완료 (사용자: {}, 경매 상품: {})", users.size(), items.size());
        return "admin";
    }

    /**
     * 사용자 정지 처리
     *
     * @param userId 정지할 사용자 ID
     * @param model  템플릿에 전달할 데이터
     * @return 관리자 페이지로 리디렉션
     */
    @PostMapping("/admin/ban-user")
    @Transactional
    public String banUser(@RequestParam("userId") Long userId, Model model) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setBanned(true); // 🔹 사용자 정지 처리
            userRepository.save(user);
            logger.info("🚫 사용자 정지됨: {} (ID: {})", user.getEmail(), userId);
        } else {
            model.addAttribute("errorMessage", "사용자를 찾을 수 없습니다.");
            return "error";
        }
        return "redirect:/admin";
    }

    /**
     * 사용자 정지 해제 처리
     *
     * @param userId 정지 해제할 사용자 ID
     * @param model  템플릿에 전달할 데이터
     * @return 관리자 페이지로 리디렉션
     */
    @PostMapping("/admin/unban-user")
    @Transactional
    public String unbanUser(@RequestParam("userId") Long userId, Model model) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setBanned(false); // 🔹 사용자 정지 해제
            userRepository.save(user);
            logger.info("✅ 사용자 정지 해제됨: {} (ID: {})", user.getEmail(), userId);
        } else {
            model.addAttribute("errorMessage", "사용자를 찾을 수 없습니다.");
            return "error";
        }
        return "redirect:/admin";
    }

    /**
     * 경매 상품 삭제 처리
     *
     * @param itemId 삭제할 경매 상품 ID
     * @param model  템플릿에 전달할 데이터
     * @return 관리자 페이지로 리디렉션
     */
    @PostMapping("/admin/delete-item")
    @Transactional
    public String deleteItem(@RequestParam("itemId") Long itemId, Model model) {
        Optional<AuctionItem> auctionItemOptional = auctionItemRepository.findById(itemId);

        if (auctionItemOptional.isPresent()) {
            AuctionItem auctionItem = auctionItemOptional.get();

            // 🔹 해당 상품의 모든 입찰 내역 삭제
            bidRepository.deleteByAuctionItem(auctionItem);

            // 🔹 경매 상품 삭제
            auctionItemRepository.delete(auctionItem);

            logger.info("🗑️ 경매 상품 삭제됨: {} (ID: {})", auctionItem.getName(), itemId);
        } else {
            model.addAttribute("errorMessage", "상품을 찾을 수 없습니다.");
            return "error";
        }
        return "redirect:/admin";
    }
}