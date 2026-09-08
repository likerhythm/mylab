package org.example.p04concurrentleaderboard.purchase.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

    @Builder.Default
    @Column(nullable = false)
    private boolean refunded = false;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Integer rank;

    protected Purchase() {
    }
}
