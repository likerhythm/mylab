package org.example.p04concurrentleaderboard.admin;

import lombok.RequiredArgsConstructor;
import org.example.p04concurrentleaderboard.leaderboard.RedisLeaderBoard;
import org.example.p04concurrentleaderboard.purchase.repository.PurchaseRepository;
import org.example.p04concurrentleaderboard.refund.repository.RefundRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final RedisLeaderBoard redisLeaderBoard;
    private final PurchaseRepository purchaseRepository;
    private final RefundRepository refundRepository;

    @Transactional
    @PostMapping("/reset")
    public ResponseEntity<Void> reset() {
        refundRepository.deleteAll();
        purchaseRepository.deleteAll();
        redisLeaderBoard.clear();
        return ResponseEntity.ok().build();
    }
}
