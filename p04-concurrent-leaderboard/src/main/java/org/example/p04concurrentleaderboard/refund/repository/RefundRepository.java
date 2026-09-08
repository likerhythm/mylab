package org.example.p04concurrentleaderboard.refund.repository;

import org.example.p04concurrentleaderboard.refund.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}
