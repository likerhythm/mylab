package org.example.p04concurrentleaderboard.refund.request;

import jakarta.validation.constraints.NotNull;

public record RefundRequest(
        @NotNull Long userId,
        @NotNull Long purchaseId
) {
}
