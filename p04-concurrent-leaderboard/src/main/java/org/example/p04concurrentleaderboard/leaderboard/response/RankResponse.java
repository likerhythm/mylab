package org.example.p04concurrentleaderboard.leaderboard.response;

public record RankResponse(
        Long userId,
        Integer rank
) {
}
