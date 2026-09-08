package org.example.p04concurrentleaderboard.purchase.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.example.p04concurrentleaderboard.event.Event;
import org.example.p04concurrentleaderboard.leaderboard.dto.LeaderBoardApplyDto;
import org.example.p04concurrentleaderboard.leaderboard.RedisLeaderBoard;
import org.example.p04concurrentleaderboard.purchase.entity.Purchase;
import org.example.p04concurrentleaderboard.purchase.repository.PurchaseRepository;
import org.example.p04concurrentleaderboard.purchase.request.PurchaseRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final RedisLeaderBoard redisLeaderBoard;

    // TODO Redis와 DB 간 원자성이 없음: Redis에는 반영했는데 DB에 실패하면??
    @Transactional
    public void submit(PurchaseRequest request) {
        Instant now = Instant.now();
        if (Event.isActive(now)) {
            applyToLeaderBoard(request, now); // Redis
        }
        savePurchase(request, now);           // Disk
    }

    private void applyToLeaderBoard(PurchaseRequest request, Instant now) {
        redisLeaderBoard.apply(new LeaderBoardApplyDto(request.userId(), request.amount(), now));
    }

    private void savePurchase(PurchaseRequest request, Instant now) {
        Purchase purchase = buildPurchase(request, now);
        purchaseRepository.save(purchase);
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
