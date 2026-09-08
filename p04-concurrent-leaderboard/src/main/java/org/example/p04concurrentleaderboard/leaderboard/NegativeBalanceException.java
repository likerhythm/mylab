package org.example.p04concurrentleaderboard.leaderboard;

public class NegativeBalanceException extends RuntimeException {

    public NegativeBalanceException(long newScore) {
        super("누적 구매액이 음수가 될 수 없습니다: " + newScore);
    }
}
