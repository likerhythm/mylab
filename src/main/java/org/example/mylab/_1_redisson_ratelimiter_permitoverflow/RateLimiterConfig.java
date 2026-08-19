package org.example.mylab._1_redisson_ratelimiter_permitoverflow;

import java.time.Duration;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

public class RateLimiterConfig {

    private static final String NAME = "permit-overflow-demo";
    private static final String PERMITS_KEY = "{" + NAME + "}:permits";
    private static final RedissonClient REDISSON = new RedissonConfig().create();

    public RRateLimiter create() {
        RRateLimiter limiter = REDISSON.getRateLimiter(NAME);

        limiter.delete();
        limiter.trySetRate(RateType.OVERALL, SimulationConfig.RATE, Duration.ofMillis(SimulationConfig.INTERVAL_MS));
        return limiter;
    }

    public long historySize() {
        return REDISSON.getScoredSortedSet(PERMITS_KEY).size();
    }

    public static void shutdown() {
        REDISSON.shutdown();
    }
}
