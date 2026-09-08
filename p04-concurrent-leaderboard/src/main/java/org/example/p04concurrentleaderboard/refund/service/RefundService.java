package org.example.p04concurrentleaderboard.refund.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.example.p04concurrentleaderboard.event.Event;
import org.example.p04concurrentleaderboard.leaderboard.RedisLeaderBoard;
import org.example.p04concurrentleaderboard.leaderboard.dto.LeaderBoardApplyDto;
import org.example.p04concurrentleaderboard.purchase.entity.Purchase;
import org.example.p04concurrentleaderboard.purchase.repository.PurchaseRepository;
import org.example.p04concurrentleaderboard.refund.Refund;
import org.example.p04concurrentleaderboard.refund.repository.RefundRepository;
import org.example.p04concurrentleaderboard.refund.request.RefundRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefundService {

    private static final String ERROR_MESSAGE = "환불할 수 있는 구매 이력이 없습니다.";

    private final RefundRepository refundRepository;
    private final PurchaseRepository purchaseRepository;
    private final RedisLeaderBoard redisLeaderBoard;

    @Transactional
    public void submit(RefundRequest request) {
        Purchase purchase = getPurchase(request.userId(), request.purchaseId());
        Instant now = Instant.now();
        if (Event.isActive(now) && purchase.inEvent()) {
            applyToLeaderBoard(request, purchase, now); // Redis
        }
        saveRefund(request, now);           // Disk
    }

    private void applyToLeaderBoard(RefundRequest request, Purchase purchase, Instant now) {
        redisLeaderBoard.apply(new LeaderBoardApplyDto(request.userId(), -purchase.getAmount(), now));
    }

    private void saveRefund(RefundRequest request, Instant now) {
        Refund refund = buildRefund(request, now);
        refundRepository.save(refund);
    }

    private Refund buildRefund(RefundRequest request, Instant now) {
        return Refund.builder()
                .purchaseId(request.purchaseId())
                .createdAt(now)
                .rank(redisLeaderBoard.getRank(request.userId()))
                .build();
    }

    private Purchase getPurchase(Long userId, Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException(ERROR_MESSAGE));
        if (!purchase.isPurchaser(userId)) {
            throw new RuntimeException(ERROR_MESSAGE);
        }
        return purchase;
    }
}
