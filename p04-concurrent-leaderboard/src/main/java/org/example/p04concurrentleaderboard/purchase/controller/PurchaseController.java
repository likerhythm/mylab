package org.example.p04concurrentleaderboard.purchase.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.p04concurrentleaderboard.purchase.request.PurchaseRequest;
import org.example.p04concurrentleaderboard.purchase.response.PurchaseResponse;
import org.example.p04concurrentleaderboard.purchase.service.PurchaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<List<PurchaseResponse>> history(@RequestParam Long userId) {
        return ResponseEntity.ok(purchaseService.getHistory(userId));
    }

    @PostMapping
    public ResponseEntity<PurchaseIdResponse> purchase(
            @RequestBody @Validated PurchaseRequest request
    ) {
        Long purchaseId = purchaseService.submit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PurchaseIdResponse(purchaseId));
    }

    record PurchaseIdResponse(Long purchaseId) {}
}
