package org.example.p04concurrentleaderboard.refund.controller;

import lombok.RequiredArgsConstructor;
import org.example.p04concurrentleaderboard.refund.request.RefundRequest;
import org.example.p04concurrentleaderboard.refund.service.RefundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/refund")
public class RefundController {

    private final RefundService refundService;

    @PostMapping
    public ResponseEntity<Void> refund(
            @RequestBody @Validated RefundRequest request
    ) {
        refundService.submit(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
