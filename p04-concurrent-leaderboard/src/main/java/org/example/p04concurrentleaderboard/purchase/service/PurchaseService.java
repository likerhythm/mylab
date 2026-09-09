package org.example.p04concurrentleaderboard.purchase.service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.p04concurrentleaderboard.event.Event;
import org.example.p04concurrentleaderboard.leaderboard.dto.LeaderBoardApplyDto;
import org.example.p04concurrentleaderboard.leaderboard.RedisLeaderBoard;
import org.example.p04concurrentleaderboard.purchase.entity.Purchase;
import org.example.p04concurrentleaderboard.purchase.repository.PurchaseRepository;
import org.example.p04concurrentleaderboard.purchase.request.PurchaseRequest;
import org.example.p04concurrentleaderboard.purchase.response.PurchaseResponse;
import org.example.p04concurrentleaderboard.refund.repository.RefundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final RefundRepository refundRepository;
    private final RedisLeaderBoard redisLeaderBoard;

    public List<PurchaseResponse> getHistory(Long userId) {
        List<Purchase> purchases = purchaseRepository.findAllByUserId(userId);
        List<Long> purchaseIds = purchases.stream().map(Purchase::getId).toList();
        Set<Long> refundedIds = refundRepository.findAllByPurchaseIdIn(purchaseIds).stream()
                .map(r -> r.getPurchaseId())
                .collect(Collectors.toSet());
        return purchases.stream()
                .map(p -> new PurchaseResponse(p.getId(), p.getAmount(), p.getCreatedAt(), refundedIds.contains(p.getId())))
                .toList();
    }

    // TODO Redis와 DB 간 원자성이 없음: Redis에는 반영했는데 DB에 실패하면??
    @Transactional
    public Long submit(PurchaseRequest request) {
        Instant now = Instant.now();
        if (Event.isActive(now)) {
            applyToLeaderBoard(request, now); // Redis
        }
        return savePurchase(request, now);    // Disk
    }

    private void applyToLeaderBoard(PurchaseRequest request, Instant now) {
        redisLeaderBoard.apply(new LeaderBoardApplyDto(request.userId(), request.amount(), now));
    }

    private Long savePurchase(PurchaseRequest request, Instant now) {
        Purchase purchase = buildPurchase(request, now);
        return purchaseRepository.save(purchase).getId();
    }

    private Purchase buildPurchase(PurchaseRequest request, Instant now) {
        return Purchase.builder()
                        .amount(request.amount())
                        .userId(request.userId())
                        .createdAt(now)
                        .rank(redisLeaderBoard.getRank(request.userId()))
                        .build();
    }
}
