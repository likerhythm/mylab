package org.example.p04concurrentleaderboard.event.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ConfigEventRequest(
        @NotNull LocalDateTime startAt,
        @NotNull LocalDateTime endAt
) {
}
