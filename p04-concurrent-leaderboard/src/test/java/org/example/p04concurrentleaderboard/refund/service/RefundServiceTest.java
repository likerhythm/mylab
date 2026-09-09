package org.example.p04concurrentleaderboard.refund.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import org.example.p04concurrentleaderboard.event.Event;
import org.example.p04concurrentleaderboard.leaderboard.RedisLeaderBoard;
import org.example.p04concurrentleaderboard.purchase.entity.Purchase;
import org.example.p04concurrentleaderboard.purchase.repository.PurchaseRepository;
import org.example.p04concurrentleaderboard.refund.repository.RefundRepository;
import org.example.p04concurrentleaderboard.refund.request.RefundRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private RedisLeaderBoard redisLeaderBoard;

    @InjectMocks
    private RefundService refundService;

    @Test
    void 이벤트_기간_전_구매_환불시_리더보드에_반영되지_않음() {
        Long userId = 1L;
        Long purchaseId = 1L;

        // 이벤트: 30분 전 ~ 30분 후
        Event.setStartAt(LocalDateTime.now().minusMinutes(30));
        Event.setEndAt(LocalDateTime.now().plusMinutes(30));

        // 구매: 이벤트 시작 전
        Purchase purchase = Purchase.builder()
                .id(purchaseId)
                .userId(userId)
                .amount(10_000L)
                .createdAt(Instant.now().minus(Duration.ofHours(2)))
                .build();

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        refundService.submit(new RefundRequest(userId, purchase.getAmount(), purchaseId));

        verify(redisLeaderBoard, never()).apply(any());
    }

    @Test
    void 이벤트_종료_후_환불시_리더보드에_반영되지_않음() {
        Long userId = 1L;
        Long purchaseId = 1L;

        // 이벤트: 2시간 전 ~ 1시간 전
        Event.setStartAt(LocalDateTime.now().minusHours(2));
        Event.setEndAt(LocalDateTime.now().minusHours(1));

        // 구매: 이벤트 기간 중
        Purchase purchase = Purchase.builder()
                .id(purchaseId)
                .userId(userId)
                .amount(10_000L)
                .createdAt(Instant.now().minus(Duration.ofMinutes(90)))
                .build();

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        refundService.submit(new RefundRequest(userId, purchase.getAmount(), purchaseId));

        verify(redisLeaderBoard, never()).apply(any());
    }

    @Test
    void 이벤트_기간_중_구매_환불시_리더보드에_반영됨() {
        Long userId = 1L;
        Long purchaseId = 1L;

        // 이벤트: 30분 전 ~ 30분 후
        Event.setStartAt(LocalDateTime.now().minusMinutes(30));
        Event.setEndAt(LocalDateTime.now().plusMinutes(30));

        // 구매: 이벤트 기간 중 (20분 전)
        Purchase purchase = Purchase.builder()
                .id(purchaseId)
                .userId(userId)
                .amount(10_000L)
                .createdAt(Instant.now().minus(Duration.ofMinutes(20)))
                .build();

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        refundService.submit(new RefundRequest(userId, purchase.getAmount(), purchaseId));

        verify(redisLeaderBoard).apply(any());
    }

    @Test
    void 다른_사람의_구매_환불시_예외_발생() {
        Long userId = 1L;
        Long othersUserId = 2L;
        Long purchaseId = 1L;

        // 구매: othersUserId 소유
        Purchase purchase = Purchase.builder()
                .id(purchaseId)
                .userId(othersUserId)
                .amount(10_000L)
                .createdAt(Instant.now().minusSeconds(10))
                .build();

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> refundService.submit(new RefundRequest(userId, purchase.getAmount(), purchaseId)))
                .isInstanceOf(RuntimeException.class);
    }
}
