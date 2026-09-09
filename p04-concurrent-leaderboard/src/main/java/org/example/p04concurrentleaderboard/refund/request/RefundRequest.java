package org.example.p04concurrentleaderboard.refund.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RefundRequest(
        @NotNull Long userId,
        @NotNull @Positive Long amount,
        @NotNull Long purchaseId
) {
}
