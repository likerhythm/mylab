package org.example.p04concurrentleaderboard.event.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.p04concurrentleaderboard.event.request.ConfigEventRequest;
import org.example.p04concurrentleaderboard.event.Event;
import org.example.p04concurrentleaderboard.event.response.EventResultEntry;
import org.example.p04concurrentleaderboard.event.service.EventService;
import org.example.p04concurrentleaderboard.leaderboard.RedisLeaderBoard;
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
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;
    private final RedisLeaderBoard redisLeaderBoard;

    @PostMapping
    public ResponseEntity<Void> configEvent(
            @RequestBody @Validated ConfigEventRequest request
    ) {
        eventService.clear();
        Event.setStartAt(request.startAt());
        Event.setEndAt(request.endAt());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/result")
    public ResponseEntity<List<EventResultEntry>> result(
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok().body(eventService.result(userId));
    }
}
