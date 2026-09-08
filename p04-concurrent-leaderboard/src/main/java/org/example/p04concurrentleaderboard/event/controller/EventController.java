package org.example.p04concurrentleaderboard.event.controller;

import lombok.RequiredArgsConstructor;
import org.example.p04concurrentleaderboard.event.request.ConfigEventRequest;
import org.example.p04concurrentleaderboard.event.Event;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {

    @PostMapping
    public ResponseEntity<Void> configEvent(
            @RequestBody @Validated ConfigEventRequest request
    ) {
        Event.setStartAt(request.startAt());
        Event.setEndAt(request.endAt());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
