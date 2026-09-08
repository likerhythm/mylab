package org.example.p04concurrentleaderboard.purchase.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.p04concurrentleaderboard.event.Event;

@Getter
@Setter
@Entity
@Builder
@Table(name = "purchase")
@AllArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Integer rank;

    protected Purchase() {
    }

    public boolean inEvent() {
        return Event.isActive(createdAt);
    }

    public boolean isPurchaser(Long userId) {
        return Objects.equals(this.userId, userId);
    }
}
