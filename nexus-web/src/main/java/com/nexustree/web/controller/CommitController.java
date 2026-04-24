package com.nexustree.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.nexustree.core.service.CommitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/repos")
@RequiredArgsConstructor
@Tag(name = "Commits", description = "Endpoints for managing data history (Creation and Retrieval)")
public class CommitController {

    private final CommitService commitService;

    @Schema(description = "Request payload to create a new commit")
    public record CommitRequest(
            @Schema(description = "Target branch name", example = "main")
            String branch,

            @Schema(description = "Message describing the changes", example = "Adding user role and level")
            String message,

            @Schema(description = "The complete JSON with the current state. The engine will calculate the diff automatically.",
                    example = "{\"name\": \"PeopleName\", \"level\": \"Senior\", \"active\": true}")
            JsonNode data) {
    }

    @Operation(summary = "Create a new Commit",
            description = "Receives a JSON payload, compares it with the latest commit on the branch, generates a Patch (Diff), and saves it to the database generating a unique SHA-256 Hash.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commit successfully created"),
            @ApiResponse(responseCode = "400", description = "No changes detected in the JSON (Commit rejected)", content = @Content)
    })
    @PostMapping("/{repoName}/commits")
    public ResponseEntity<Map<String, String>> createCommit(
            @PathVariable @Schema(description = "Repository name", example = "user-profile") String repoName,
            @RequestBody CommitRequest request) {

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
    }

    @Operation(summary = "Retrieve JSON state (Time Travel)",
            description = "Reconstructs the exact JSON state at a specific point in time by applying all historical patches from the Genesis commit up to the provided Hash.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JSON state successfully reconstructed"),
            @ApiResponse(responseCode = "500", description = "Hash not found or corrupted history chain", content = @Content)
    })
    @GetMapping("/commits/{hash}")
    public ResponseEntity<JsonNode> getCommitState(
            @PathVariable @Schema(description = "The SHA-256 Hash of the desired commit") String hash
    ) {
        JsonNode state = commitService.reconstructState(hash);
        return ResponseEntity.ok(state);
    }
}
