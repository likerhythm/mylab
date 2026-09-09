package org.example.p04concurrentleaderboard.purchase.response;

import java.time.Instant;

public record PurchaseResponse(
        Long id,
        Long amount,
        Instant createdAt,
        boolean refunded
) {
}
