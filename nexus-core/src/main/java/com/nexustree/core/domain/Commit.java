package com.nexustree.core.domain;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

public record Commit(
        String hash,
        String parentHash,
        String message,
        Instant timestamp,
        JsonNode patchData
) {
}
