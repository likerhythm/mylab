package org.example.p04concurrentleaderboard.refund;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "refund")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long purchaseId;

    @Column(nullable = false)
    private Instant createdAt;

    /**
     * 구매 반영 후 랭킹
     */
    @Column
    private Integer rank;

    protected Refund() {
    }

    public Refund(Long purchaseId, Instant createdAt) {
        this.purchaseId = purchaseId;
        this.createdAt = createdAt;
    }
}
