package com.auction.service;

import com.auction.domain.AuctionItem;
import com.auction.domain.Bid;
import com.auction.domain.User;
import com.auction.repository.AuctionItemRepository;
import com.auction.repository.BidRepository;
import com.auction.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 🎯 경매 상품 서비스
 * - 경매 종료 시 낙찰자 결정
 * - 낙찰된 상품의 구매 확정 처리
 * - 이메일 알림 발송 기능 포함
 */
@Service
public class AuctionItemService {

    private final AuctionItemRepository auctionItemRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    public AuctionItemService(AuctionItemRepository auctionItemRepository,
                              BidRepository bidRepository,
                              UserRepository userRepository,
                              MailService mailService) {
        this.auctionItemRepository = auctionItemRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    /**
     * 🔹 마감된 경매의 낙찰자를 결정하는 메서드
     * - 1분마다 실행 (스케줄링)
     */
    @Transactional
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void processAuctionEndings() {
        List<AuctionItem> endedAuctions = auctionItemRepository.findByEndTimeBeforeAndWinnerIsNull(LocalDateTime.now());

        for (AuctionItem item : endedAuctions) {
            Bid highestBid = bidRepository.findTopByAuctionItemOrderByBidAmountDesc(item);
            item.setWinner((highestBid != null) ? highestBid.getBidder() : null); // 낙찰자 설정
            auctionItemRepository.save(item);
        }
    }

    /**
     * 🔹 구매 확정 처리
     * - 낙찰자가 구매 확정을 진행하면, 판매자에게 포인트 지급
     *
     * @param itemId 구매 확정할 경매 상품 ID
     * @param buyer 구매 확정을 진행하는 사용자
     */
    @Transactional
    public void confirmPurchase(Long itemId, User buyer) {
        AuctionItem auctionItem = auctionItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 경매 상품을 찾을 수 없습니다."));

        if (auctionItem.isPurchased()) {
            throw new IllegalStateException("이미 구매 확정된 상품입니다.");
        }

        if (!auctionItem.getWinner().equals(buyer)) {
            throw new IllegalStateException("해당 상품의 낙찰자가 아닙니다.");
        }

        Bid highestBid = bidRepository.findTopByAuctionItemOrderByBidAmountDesc(auctionItem);
        if (highestBid == null) {
            throw new IllegalStateException("유효한 입찰 내역이 없습니다.");
        }

        int finalPrice = highestBid.getBidAmount();
        User seller = auctionItem.getSeller();

        // ✅ 판매자에게 포인트 지급
        seller.setPoints(seller.getPoints() + finalPrice);
        userRepository.save(seller);

        auctionItem.setPurchased(true);
        auctionItemRepository.save(auctionItem);

        System.out.println("✅ 구매 확정 완료: " + auctionItem.getName());
    }

    /**
     * 🔹 특정 판매자가 등록한 경매 상품 목록 조회
     *
     * @param seller 판매자
     * @return 판매자가 등록한 상품 목록
     */
    @Transactional(readOnly = true)
    public List<AuctionItem> getAuctionItemsBySeller(User seller) {
        return auctionItemRepository.findBySeller(seller);
    }

    /**
     * 🔹 특정 사용자가 낙찰받은 상품 목록 조회
     *
     * @param winner 낙찰자
     * @return 낙찰받은 상품 목록
     */
    @Transactional(readOnly = true)
    public List<AuctionItem> getAuctionItemsByWinner(User winner) {
        return auctionItemRepository.findByWinner(winner);
    }

    /**
     * 🔹 경매 종료 후 낙찰자 결정 및 이메일 전송
     *
     * @param itemId 종료된 경매 상품 ID
     */
    @Transactional
    public void finalizeAuction(Long itemId) {
        AuctionItem auctionItem = auctionItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("경매 상품을 찾을 수 없습니다."));

        if (!auctionItem.isAuctionEnded()) {
            throw new IllegalStateException("경매가 아직 종료되지 않았습니다.");
        }

        // ✅ 최고 입찰자 찾기
        Bid highestBid = bidRepository.findTopByAuctionItemOrderByBidAmountDesc(auctionItem);
        if (highestBid == null) {
            System.out.println("🚨 낙찰자가 없습니다.");
            return; // 입찰자가 없으면 낙찰자 없음
        }

        User winner = highestBid.getBidder();
        auctionItem.setWinner(winner);
        auctionItemRepository.save(auctionItem);

        // ✅ 낙찰자에게 이메일 전송
        sendWinningEmail(winner, auctionItem, highestBid.getBidAmount());
    }

    /**
     * 🔹 낙찰자에게 이메일 알림을 전송
     *
     * @param winner 낙찰자
     * @param auctionItem 낙찰된 상품
     * @param finalPrice 낙찰 가격
     */
    private void sendWinningEmail(User winner, AuctionItem auctionItem, int finalPrice) {
        String subject = "🎉 축하합니다! 경매 낙찰 안내";
        String message = "<h2>안녕하세요, " + winner.getName() + "님!</h2>"
                + "<p>귀하가 입찰한 <strong>" + auctionItem.getName() + "</strong> 경매가 종료되었습니다.</p>"
                + "<p>낙찰 가격: <strong>" + finalPrice + "P</strong></p>"
                + "<p>구매 확정을 진행하려면 아래 링크를 클릭해주세요.</p>"
                + "<a href='http://localhost:8080/auction-item/" + auctionItem.getId() + "'>상품 상세 페이지로 이동</a>";

        mailService.sendEmail(winner.getEmail(), subject, message);
    }
}