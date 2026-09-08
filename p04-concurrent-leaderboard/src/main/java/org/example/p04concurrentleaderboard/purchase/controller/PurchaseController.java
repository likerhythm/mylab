package org.example.p04concurrentleaderboard.purchase.controller;

import lombok.RequiredArgsConstructor;
import org.example.p04concurrentleaderboard.purchase.PurchaseRequest;
import org.example.p04concurrentleaderboard.purchase.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<Void> purchase(
            @RequestBody @Validated PurchaseRequest request
    ) {
        purchaseService.submit(request);
        return ResponseEntity.ok().build();
    }
}
