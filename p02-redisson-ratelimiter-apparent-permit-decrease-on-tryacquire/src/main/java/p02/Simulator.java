package p02;

import p02.config.RedissonConfig;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import p02.config.RateLimiterConfig;
import p02.config.SimulationConfig;
import org.redisson.api.RBucket;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;

public class Simulator {

    public void run() throws InterruptedException {
        RateLimiterConfig config = new RateLimiterConfig();
        RRateLimiter limiter = config.create();

        long startNano = System.nanoTime();
        long endAt = System.nanoTime() + SimulationConfig.DURATION_SEC * 1_000_000_000L;

        List<long[]> samples = new ArrayList<>();

        while (System.nanoTime() < endAt) {
            limiter.tryAcquire(1);
            Thread.sleep(SimulationConfig.RELEASE_DELAY_MS);
            limiter.release(1);
            long permits = getPermit();
            long tMs = (System.nanoTime() - startNano) / 1_000_000;
            long history = config.historySize();
            samples.add(new long[]{tMs, permits, history});
            Thread.sleep(10);
        }

        writeJson(samples);
    }

    private long getPermit() {
        String valueKey = RateLimiterConfig.valueKey();
        RedissonClient redisson = RedissonConfig.REDISSON;
        RBucket<Long> value = redisson.getBucket(valueKey, LongCodec.INSTANCE);
        return value.get();
    }

    private void writeJson(List<long[]> samples) {
        StringJoiner arr = new StringJoiner(",\n    ");
        for (long[] s : samples) {
            arr.add(String.format("{\"t\":%d,\"permits\":%d,\"history\":%d}", s[0], s[1], s[2]));
        }
        String json = "{\n"
                + "  \"config\": {\"rate\":" + SimulationConfig.RATE
                +               ",\"intervalMs\":" + SimulationConfig.INTERVAL_MS
                +               ",\"releaseDelayMs\":" + SimulationConfig.RELEASE_DELAY_MS
                +               ",\"durationSec\":" + SimulationConfig.DURATION_SEC + "},\n"
                + "  \"samples\": [\n    " + arr + "\n  ]\n"
                + "}\n";
        try {
            String packagePath = getClass().getPackageName().replace('.', '/');
            Path dir = moduleRoot().resolve(Path.of("src", "main", "java", packagePath));
            Files.createDirectories(dir);
            Path out = dir.resolve("permit-history.json");
            Files.writeString(out, json);
            System.out.println("저장됨: " + out.toAbsolutePath());
        } catch (Exception e) {
            System.out.println("파일 저장 실패: " + e.getMessage());
        }
    }

    private Path moduleRoot() {
        try {
            Path p = Path.of(getClass().getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            while (p != null && !Files.isDirectory(p.resolve("src"))) {
                p = p.getParent();
            }
            if (p != null) {
                return p;
            }
        } catch (Exception e) {
        }
        return Path.of("").toAbsolutePath();
    }
}
