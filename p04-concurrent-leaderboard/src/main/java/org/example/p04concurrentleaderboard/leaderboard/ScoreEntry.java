package org.example.p04concurrentleaderboard.leaderboard;

import java.time.Instant;

public record ScoreEntry(
        Long userId,
        Long score,
        Instant updatedAt
) {
}
