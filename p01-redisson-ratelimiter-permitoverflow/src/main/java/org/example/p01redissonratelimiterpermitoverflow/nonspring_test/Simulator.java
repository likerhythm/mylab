package org.example.p01redissonratelimiterpermitoverflow.nonspring_test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.example.p01redissonratelimiterpermitoverflow.nonspring_test.config.RateLimiterConfig;
import org.example.p01redissonratelimiterpermitoverflow.nonspring_test.config.SimulationConfig;
import org.redisson.api.RRateLimiter;

public class Simulator {

    public void run() throws InterruptedException {
        RateLimiterConfig config = new RateLimiterConfig();
        RRateLimiter limiter = config.create();
        System.out.println("시작 permits = " + limiter.availablePermits());

        long startNano = System.nanoTime();
        long endAt = System.nanoTime() + SimulationConfig.DURATION_SEC * 1_000_000_000L;
        long count = 0;

        List<long[]> samples = new ArrayList<>();

        while (System.nanoTime() < endAt) {
            limiter.tryAcquire(1);
            Thread.sleep(SimulationConfig.RELEASE_DELAY_MS);
            limiter.release(1);
            long permits = limiter.availablePermits();
            long tMs = (System.nanoTime() - startNano) / 1_000_000;
            long history = config.historySize();
            samples.add(new long[]{tMs, permits, history});
            count++;
        }

        System.out.printf("종료 permits = %d (반복 %d회, 샘플 %d개)%n", limiter.availablePermits(), count, samples.size());
        writeJson(samples);
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
