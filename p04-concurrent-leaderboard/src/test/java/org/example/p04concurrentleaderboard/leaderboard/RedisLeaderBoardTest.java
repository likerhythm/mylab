package org.example.p04concurrentleaderboard.leaderboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;

@ExtendWith(MockitoExtension.class)
class RedisLeaderBoardTest {

    @Mock
    private RedissonClient redisson;

    @Mock
    private RScoredSortedSet<String> ranking;

    @Mock
    private RMap<Long, String> memberInfo;

    private RedisLeaderBoard leaderBoard;

    @BeforeEach
    void setUp() {
        doReturn(ranking).when(redisson).getScoredSortedSet("ranking");
        doReturn(memberInfo).when(redisson).getMap("memberInfo");
        leaderBoard = new RedisLeaderBoard(redisson);
    }

    @Test
    void 이벤트_기간_중_첫_구매시_NPE_없이_정상_처리() {
        Long userId = 1L;
        when(memberInfo.get(userId)).thenReturn(null);

        LeaderBoardApplyDto dto = new LeaderBoardApplyDto(userId, 10_000L, Instant.now());

        assertThatNoException().isThrownBy(() -> leaderBoard.apply(dto));
    }

    @Test
    void 동일_유저_동시_구매시_ranking에_member가_하나만_존재함() throws InterruptedException {
        int threadCount = 10;
        Long userId = 1L;

        ConcurrentHashMap<Long, String> fakeMembers = new ConcurrentHashMap<>();
        Set<String> fakeRanking = Collections.synchronizedSet(new HashSet<>());

        when(memberInfo.get(userId)).thenAnswer(inv -> fakeMembers.get(userId));
        when(memberInfo.put(eq(userId), anyString())).thenAnswer(inv -> fakeMembers.put(userId, inv.getArgument(1)));
        when(ranking.add(anyDouble(), anyString())).thenAnswer(inv -> fakeRanking.add(inv.getArgument(1)));
        doAnswer(inv -> fakeRanking.remove(inv.getArgument(0))).when(ranking).remove(anyString());
        when(ranking.getScore(anyString())).thenReturn(0.0);

        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                ready.countDown();
                try {
                    start.await();
                    leaderBoard.apply(new LeaderBoardApplyDto(userId, 1_000L, Instant.now()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }

        ready.await();
        start.countDown();
        done.await();

        assertThat(fakeRanking).hasSize(1);
    }

    @Test
    void 누적_구매액이_음수가_되면_NegativeBalanceException_던짐() {
        Long userId = 1L;
        when(memberInfo.get(userId)).thenReturn(null);

        LeaderBoardApplyDto dto = new LeaderBoardApplyDto(userId, -10_000L, Instant.now());

        assertThatThrownBy(() -> leaderBoard.apply(dto)).isInstanceOf(NegativeBalanceException.class);
    }
}
