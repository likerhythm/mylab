package p02.config;

import java.time.Duration;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

public class RateLimiterConfig {

    private static final String NAME = "permit-decrease-demo";
    private static final String PERMITS_KEY = "{" + NAME + "}:permits";
    private static final String VALUE_KEY = "{" + NAME + "}:value";
    private static final RedissonClient REDISSON = RedissonConfig.REDISSON;

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

    public static String valueKey() {
        return VALUE_KEY;
    }
}
