package org.example.p04concurrentleaderboard.leaderboard.dto;

import java.time.Instant;

public record ScoreEntry(
        Long userId,
        Long score,
        Instant updatedAt
) {
}
