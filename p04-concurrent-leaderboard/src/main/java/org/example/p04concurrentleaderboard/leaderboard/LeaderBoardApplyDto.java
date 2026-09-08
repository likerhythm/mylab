package org.example.p04concurrentleaderboard.leaderboard;

import java.time.Instant;

public record LeaderBoardApplyDto(
        Long userId,
        Long diff,
        Instant instant
) {
}
