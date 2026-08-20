package org.example.p01redissonratelimiterpermitoverflow.nonspring_test.config;

public class SimulationConfig {

    /** permit 상한 */
    public static final int RATE = 10;

    /** permit 복구 간격 (ms) */
    public static final int INTERVAL_MS = 1000;

    /** permit 소비 후 release까지 간격 (ms) */
    public static final int RELEASE_DELAY_MS = 100;

    /** 총 실행 시간 (sec) */
    public static final int DURATION_SEC = 30;

    private SimulationConfig() {}
}
