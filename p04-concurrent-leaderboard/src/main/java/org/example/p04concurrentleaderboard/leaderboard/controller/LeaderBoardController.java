package org.example.p04concurrentleaderboard.leaderboard.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.p04concurrentleaderboard.leaderboard.dto.LeaderBoardEntry;
import org.example.p04concurrentleaderboard.leaderboard.response.RankResponse;
import org.example.p04concurrentleaderboard.leaderboard.service.LeaderBoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leaderboard")
public class LeaderBoardController {

    private final LeaderBoardService leaderBoardService;

    // 상위 100명 조회
    @GetMapping("/top100")
    public ResponseEntity<List<LeaderBoardEntry>> top100() {
        return ResponseEntity.ok().body(leaderBoardService.top100());
    }

    // 내 순위 조회
    @GetMapping("/my-rank")
    public ResponseEntity<RankResponse> myRank(
            @RequestParam Long userId
    ) {
        int rank = leaderBoardService.rank(userId);
        return ResponseEntity.ok().body(new RankResponse(userId, rank));
    }
}
