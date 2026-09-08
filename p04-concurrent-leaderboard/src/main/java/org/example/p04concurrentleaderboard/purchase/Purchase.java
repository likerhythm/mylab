package org.example.p04concurrentleaderboard.purchase;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private boolean refunded = false;

    @Column(nullable = false)
    private Instant createdAt;

    protected Purchase() {
    }

    public Purchase(Long userId, long amount, Instant createdAt) {
        this.userId = userId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public void markRefunded() {
        this.refunded = true;
    }
}
