package org.example.p04concurrentleaderboard.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void 마감_직전_요청은_수락됨() {
        LocalDateTime end = LocalDateTime.now().plusSeconds(10);
        Event.setStartAt(LocalDateTime.now().minusHours(1));
        Event.setEndAt(end);

        Instant justBefore = end.atZone(ZoneId.systemDefault()).toInstant().minusMillis(1);

        assertThat(Event.isActive(justBefore)).isTrue();
    }

    @Test
    void 마감_정각_요청은_거부됨() {
        LocalDateTime end = LocalDateTime.now().plusSeconds(10);
        Event.setStartAt(LocalDateTime.now().minusHours(1));
        Event.setEndAt(end);

        Instant exactly = end.atZone(ZoneId.systemDefault()).toInstant();

        assertThat(Event.isActive(exactly)).isFalse();
    }
}
