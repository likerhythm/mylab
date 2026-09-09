package org.example.p04concurrentleaderboard.leaderboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LeaderBoardViewController {

    @GetMapping("/")
    public String index() {
        return "leaderboard";
    }
}
