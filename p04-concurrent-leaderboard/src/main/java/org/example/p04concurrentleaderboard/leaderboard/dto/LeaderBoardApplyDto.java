package org.example.p04concurrentleaderboard.leaderboard.dto;

import java.time.Instant;

public record LeaderBoardApplyDto(
        Long userId,
        Long diff,
        Instant instant
) {
}
