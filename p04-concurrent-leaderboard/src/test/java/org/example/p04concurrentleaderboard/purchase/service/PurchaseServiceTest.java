package org.example.p04concurrentleaderboard.purchase.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import org.example.p04concurrentleaderboard.event.Event;
import org.example.p04concurrentleaderboard.leaderboard.RedisLeaderBoard;
import org.example.p04concurrentleaderboard.purchase.repository.PurchaseRepository;
import org.example.p04concurrentleaderboard.purchase.request.PurchaseRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private RedisLeaderBoard redisLeaderBoard;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void 이벤트_기간_외_구매는_리더보드에_반영되지_않음() {
        Event.setStartAt(LocalDateTime.now().minusHours(2));
        Event.setEndAt(LocalDateTime.now().minusHours(1));

        purchaseService.submit(new PurchaseRequest(1L, 10_000L));

        verify(redisLeaderBoard, never()).apply(any());
    }

    @Test
    void 이벤트_기간_중_구매는_리더보드에_반영됨() {
        Event.setStartAt(LocalDateTime.now().minusHours(1));
        Event.setEndAt(LocalDateTime.now().plusHours(1));

        purchaseService.submit(new PurchaseRequest(1L, 10_000L));

        verify(redisLeaderBoard).apply(any());
    }
}
