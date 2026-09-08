package org.example.p04concurrentleaderboard.purchase.repository;

import org.example.p04concurrentleaderboard.purchase.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
