package com.nexustree.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.nexustree.web.service.CommitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/repos")
@RequiredArgsConstructor
public class CommitController {

    private final CommitService commitService;

    public record CommitRequest(String branch, String message, JsonNode data) {}

    @PostMapping("/{repoName}/commits")
    public ResponseEntity<Map<String, String>> createCommit(
            @PathVariable String repoName,
            @RequestBody CommitRequest request) {

        try {
            String hash = commitService.createCommit(
                    repoName,
                    request.branch(),
                    request.message(),
                    request.data()
            );
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "commit_hash", hash,
                    "message", "Commit successfully created!"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/commits/{hash}")
    public ResponseEntity<JsonNode> getCommitState(@PathVariable String hash) {
        JsonNode state = commitService.reconstructState(hash);
        return ResponseEntity.ok(state);
    }
}
