package com.nexustree.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nexustree.core.crypto.HashGenerator;
import com.nexustree.core.diff.JsonDiffer;
import com.nexustree.core.diff.JsonPatcher;
import com.nexustree.core.domain.Commit;
import com.nexustree.core.exception.NoChangesDetectedException;
import com.nexustree.core.port.BranchPort;
import com.nexustree.core.port.CommitPort;
import com.nexustree.core.port.RepositoryPort;

import java.time.Instant;
import java.util.List;

public class CommitService {

    private final RepositoryPort repositoryPort;
    private final BranchPort branchPort;
    private final CommitPort commitPort;

    private final JsonDiffer differ;
    private final JsonPatcher patcher;

    public CommitService(RepositoryPort repositoryPort, BranchPort branchPort, CommitPort commitPort) {
        this.repositoryPort = repositoryPort;
        this.branchPort = branchPort;
        this.commitPort = commitPort;
        this.differ = new JsonDiffer();
        this.patcher = new JsonPatcher();
    }

    public String createCommit(String repoName, String branchName, String message, JsonNode incomingJson) {

        repositoryPort.ensureRepositoryExists(repoName);
        String parentHash = branchPort.getHeadCommitHash(repoName, branchName);

        JsonNode patchDataToSave;

        if (parentHash == null) {
            patchDataToSave = incomingJson;
        } else {
            JsonNode currentState = reconstructState(parentHash);
            patchDataToSave = differ.generateDiff(currentState, incomingJson);

            if (patchDataToSave.isEmpty()) {
                throw new NoChangesDetectedException("No changes detected in the payload. Commit rejected.");
            }
        }

        String rawDataToHash = patchDataToSave.toString() + message + Instant.now().toString();
        String newHash = HashGenerator.generateSha256(rawDataToHash);

        Commit newCommit = new Commit(newHash, parentHash, message, Instant.now(), patchDataToSave);

        commitPort.save(newCommit, repoName);
        branchPort.updateHead(repoName, branchName, newHash);

        return newHash;
    }

    public JsonNode reconstructState(String commitHash) {
        List<Commit> history = commitPort.getHistoryChain(commitHash);

        JsonNode state = history.get(0).patchData().deepCopy();

        for (int i = 1; i < history.size(); i++) {
            state = patcher.applyPatch(state, (ArrayNode) history.get(i).patchData());
        }

        return state;
    }
}