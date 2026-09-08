package org.example.p04concurrentleaderboard.leaderboard;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.time.Instant;
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
    void 누적_구매액이_음수가_되면_NegativeBalanceException_던짐() {
        Long userId = 1L;
        when(memberInfo.get(userId)).thenReturn(null);

        LeaderBoardApplyDto dto = new LeaderBoardApplyDto(userId, -10_000L, Instant.now());

        assertThatThrownBy(() -> leaderBoard.apply(dto)).isInstanceOf(NegativeBalanceException.class);
    }
}
