package org.example.p04concurrentleaderboard.event.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.example.p04concurrentleaderboard.event.Event;
import org.example.p04concurrentleaderboard.leaderboard.dto.LeaderBoardEntry;
import org.example.p04concurrentleaderboard.purchase.entity.Purchase;
import org.example.p04concurrentleaderboard.purchase.repository.PurchaseRepository;
import org.example.p04concurrentleaderboard.refund.Refund;
import org.example.p04concurrentleaderboard.refund.repository.RefundRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final PurchaseRepository purchaseRepository;
    private final RefundRepository refundRepository;

    public List<LeaderBoardEntry> result(Long userId) {
        List<Purchase> purchases = purchaseRepository.findAllByUserId(userId).stream()
                .filter(Purchase::inEvent)
                .toList();
        List<Long> purchaseIds = getPurchaseIds(purchases);
        List<Refund> refunds = refundRepository.findAllByPurchaseIdIn(purchaseIds);
        List<Node> nodes = convertToNode(userId, purchases, refunds);
        return convertToEntry(nodes);
    }

    private List<Long> getPurchaseIds(List<Purchase> purchases) {
        List<Long> purchaseIds = purchases.stream()
                .map(Purchase::getId)
                .toList();
        return purchaseIds;
    }

    private List<Node> convertToNode(Long userId, List<Purchase> purchases, List<Refund> refunds) {
        List<Node> nodes = Stream.concat(
                        purchases.stream()
                                .map(p -> new Node(userId, p.getAmount(), p.getCreatedAt())),
                        refunds.stream()
                                .map(r -> new Node(userId, -r.getAmount(), r.getCreatedAt()))
                )
                .sorted(Comparator.comparing(Node::createdAt))
                .toList();
        return nodes;
    }

    private List<LeaderBoardEntry> convertToEntry(List<Node> nodes) {
        List<LeaderBoardEntry> entries = new ArrayList<>(nodes.size());
        long cumulativeScore = 0;
        for (Node node : nodes) {
            cumulativeScore += node.amount();
            entries.add(new LeaderBoardEntry(node.userId(), cumulativeScore, node.createdAt()));
        }
        return entries;
    }

    private record Node(
            Long userId,
            Long amount,
            Instant createdAt
    ) {
    }
}
