package org.example.p04concurrentleaderboard.event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class Event {

    private static final ZoneId ZONE = ZoneId.systemDefault();

    private static Instant startAt;
    private static Instant endAt;

    public static boolean isActive(Instant instant) {
        if (startAt == null || endAt == null) {
            return false;
        }
        return !instant.isBefore(startAt) && !instant.isAfter(endAt);
    }

    public static void setStartAt(LocalDateTime startTime) {
        startAt = startTime.atZone(ZONE).toInstant();
    }

    public static void setEndAt(LocalDateTime endTime) {
        endAt = endTime.atZone(ZONE).toInstant();
    }
}
