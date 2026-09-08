package org.example.p04concurrentleaderboard.purchase.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseRequest(
        @NotNull Long userId,
        @NotNull @Positive Long amount
) {
}
