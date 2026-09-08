package org.example.p04concurrentleaderboard.purchase.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.example.p04concurrentleaderboard.leaderboard.LeaderBoardApplyDto;
import org.example.p04concurrentleaderboard.leaderboard.RedisLeaderBoard;
import org.example.p04concurrentleaderboard.purchase.Purchase;
import org.example.p04concurrentleaderboard.purchase.PurchaseRepository;
import org.example.p04concurrentleaderboard.purchase.PurchaseRequest;
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
        // TODO 이벤트 마감 기간 확인 필요
        Instant now = Instant.now();
        applyToLeaderBoard(request, now); // Redis
        savePurchase(request, now);       // Disk
    }

    private void applyToLeaderBoard(PurchaseRequest request, Instant now) {
        redisLeaderBoard.apply(new LeaderBoardApplyDto(request.userId(), request.amount(), now));
    }

    private void savePurchase(PurchaseRequest request, Instant now) {
        Purchase purchase = new Purchase(request.userId(), request.amount(), now);
        purchaseRepository.save(purchase);
    }
}
