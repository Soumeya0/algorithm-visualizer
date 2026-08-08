package com.visualizer.algorithm.controller;

import com.visualizer.algorithm.dto.LeaderboardEntry;
import com.visualizer.algorithm.dto.RunResultRequest;
import com.visualizer.algorithm.dto.RunResultResponse;
import com.visualizer.algorithm.service.RunResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class RunResultController {

    @Autowired
    private RunResultService runResultService;

    @PostMapping("/api/runs")
    public ResponseEntity<?> logRun(@RequestBody RunResultRequest request, Authentication authentication) {
        try {
            RunResultResponse response = runResultService.logRun(authentication.getName(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/leaderboard")
    public ResponseEntity<?> getLeaderboard(@RequestParam String algorithm, @RequestParam int size) {
        try {
            List<LeaderboardEntry> leaderboard = runResultService.getLeaderboard(algorithm, size);
            return ResponseEntity.ok(leaderboard);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
