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

import java.util.List;
import java.util.Optional;

/**
 * 경매 상품 서비스 (낙찰자 결정 기능 포함)
 */
@Service
public class AuctionItemService {

    private final AuctionItemRepository auctionItemRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;

    public AuctionItemService(AuctionItemRepository auctionItemRepository, BidRepository bidRepository, UserRepository userRepository) {
        this.auctionItemRepository = auctionItemRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
    }

    /**
     * 마감된 경매의 낙찰자를 결정하는 메서드
     */
    @Transactional
    @Scheduled(fixedRate = 5000) // 1분마다 실행
    public void processAuctionEndings() {
        List<AuctionItem> endedAuctions = auctionItemRepository.findByEndTimeBeforeAndWinnerIsNull(java.time.LocalDateTime.now());

        for (AuctionItem item : endedAuctions) {
            Bid highestBid = bidRepository.findTopByAuctionItemOrderByBidAmountDesc(item);
            if (highestBid != null) {
                item.setWinner(highestBid.getBidder());
            } else {
                item.setWinner(null); // 유찰 처리
            }
            auctionItemRepository.save(item);
        }
    }

    /**
     * 🔹 낙찰자가 상품 구매 확정
     *
     * @param auctionItemId 경매 상품 ID
     * @param buyer 낙찰자
     */
    @Transactional
    public void confirmPurchase(Long auctionItemId, User buyer) {
        Optional<AuctionItem> auctionItemOptional = auctionItemRepository.findById(auctionItemId);

        if (auctionItemOptional.isPresent()) {
            AuctionItem auctionItem = auctionItemOptional.get();

            // 이미 구매 확정된 상품인지 확인
            if (auctionItem.isPurchased()) {
                throw new IllegalStateException("이미 구매 확정된 상품입니다.");
            }

            // 구매자가 낙찰자인지 확인
            if (!auctionItem.getWinner().equals(buyer)) {
                throw new IllegalStateException("이 상품의 낙찰자만 구매 확정할 수 있습니다.");
            }

            // ✅ 포인트 차감을 제거 (이미 입찰 시 차감되었음)
            // int finalPrice = auctionItem.getStartPrice();
            // if (buyer.getPoints() < finalPrice) {
            //    throw new IllegalStateException("포인트가 부족합니다.");
            // }

            // buyer.setPoints(buyer.getPoints() - finalPrice);
            // userRepository.save(buyer);

            // 구매 확정 처리
            auctionItem.confirmPurchase();
            auctionItemRepository.save(auctionItem);
        } else {
            throw new IllegalArgumentException("경매 상품을 찾을 수 없습니다.");
        }
    }

    /**
     * 특정 판매자가 등록한 경매 상품 목록 조회
     *
     * @param seller 판매자
     * @return 판매자가 등록한 상품 목록
     */
    @Transactional(readOnly = true)
    public List<AuctionItem> getAuctionItemsBySeller(User seller) {
        return auctionItemRepository.findBySeller(seller);
    }

    /**
     * 특정 사용자가 낙찰받은 상품 목록 조회
     *
     * @param winner 낙찰자
     * @return 낙찰받은 상품 목록
     */
    @Transactional(readOnly = true)
    public List<AuctionItem> getAuctionItemsByWinner(User winner) {
        return auctionItemRepository.findByWinner(winner);
    }

}