package org.example.p04concurrentleaderboard.leaderboard;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.example.p04concurrentleaderboard.leaderboard.dto.LeaderBoardApplyDto;
import org.example.p04concurrentleaderboard.leaderboard.dto.ScoreEntry;
import org.example.p04concurrentleaderboard.leaderboard.exception.NegativeBalanceException;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class RedisLeaderBoard {

    private final RScoredSortedSet<String> ranking;
    private final RMap<Long, String> memberInfo;
    private final ConcurrentHashMap<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

    public RedisLeaderBoard(RedissonClient redisson) {
        this.ranking = redisson.getScoredSortedSet("ranking");
        this.memberInfo = redisson.getMap("memberInfo");
    }

    public void apply(LeaderBoardApplyDto dto) {
        ReentrantLock lock = locks.computeIfAbsent(dto.userId(), id -> new ReentrantLock());
        lock.lock();
        try {
            String oldMember = memberInfo.get(dto.userId());
            Long newScore = dto.diff();
            if (oldMember != null) {
                newScore += ranking.getScore(oldMember).longValue() + dto.diff();
            }
            if (newScore < 0) {
                throw new NegativeBalanceException(newScore);
            }
            ScoreEntry newEntry = new ScoreEntry(dto.userId(), newScore, dto.instant());
            doAdd(newEntry);
        } finally {
            lock.unlock();
        }
    }

    public int getRank(Long userId) {
        String member = memberInfo.get(userId);
        Integer rank = ranking.revRank(member);
        return rank == null ? -1 : rank + 1;
    }

    public long getScore(Long userId) {
        String member = memberInfo.get(userId);
        return ranking.getScore(member).longValue();
    }

    private void doAdd(ScoreEntry newEntry) {
        String newMember = buildMember(newEntry);

        String oldMember = memberInfo.get(newEntry.userId());
        if (oldMember != null) {
            ranking.remove(oldMember);
        }

        ranking.add(newEntry.score(), newMember);
        memberInfo.put(newEntry.userId(), newMember);
    }

    private String buildMember(ScoreEntry entry) {
        long reversedTime = getReversedTime(entry);
        return String.format("%019d:%d", reversedTime, entry.userId());
    }

    private long getReversedTime(ScoreEntry entry) {
        return Long.MAX_VALUE - entry.updatedAt().toEpochMilli();
    }

}
