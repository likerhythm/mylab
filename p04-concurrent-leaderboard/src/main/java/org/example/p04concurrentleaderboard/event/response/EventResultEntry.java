package org.example.p04concurrentleaderboard.event.response;

import java.time.Instant;

public record EventResultEntry(
        Long userId,
        Long score,
        Long diff,
        Integer rank,
        Instant updatedAt
) {
}
