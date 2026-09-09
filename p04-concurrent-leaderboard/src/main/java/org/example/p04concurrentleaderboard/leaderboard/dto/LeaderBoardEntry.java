package org.example.p04concurrentleaderboard.leaderboard.dto;

import java.time.Instant;
import org.redisson.client.protocol.ScoredEntry;

public record LeaderBoardEntry(
        Long userId,
        Long score,
        Instant updatedAt
) {
    public static LeaderBoardEntry parse(ScoredEntry<String> entry) {
        String member = entry.getValue();
        String[] parts = member.split(":", 2);
        long reversedTime = Long.parseLong(parts[0]);
        Long userId = Long.parseLong(parts[1]);
        Long score = entry.getScore().longValue();
        Instant updatedAt = Instant.ofEpochMilli(Long.MAX_VALUE - reversedTime);
        return new LeaderBoardEntry(userId, score, updatedAt);
    }
}
