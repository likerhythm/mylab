package org.example.p04concurrentleaderboard.purchase.repository;

import java.util.List;
import org.example.p04concurrentleaderboard.purchase.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findAllByUserId(Long userId);
}
