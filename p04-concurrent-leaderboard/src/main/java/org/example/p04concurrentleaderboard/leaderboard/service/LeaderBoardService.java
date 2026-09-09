package org.example.p04concurrentleaderboard.leaderboard.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.p04concurrentleaderboard.leaderboard.RedisLeaderBoard;
import org.example.p04concurrentleaderboard.leaderboard.dto.LeaderBoardEntry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaderBoardService {

    private final RedisLeaderBoard redisLeaderBoard;

    public List<LeaderBoardEntry> top100() {
        return redisLeaderBoard.getTop100();
    }

    public int rank(Long userId) {
        int rank = redisLeaderBoard.getRank(userId);
        if (rank == -1) {
            throw new RuntimeException("구매 이력이 없는 사용자입니다.");
        }
        return rank;
    }
}
