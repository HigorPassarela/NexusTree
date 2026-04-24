package com.nexustree.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nexustree.core.crypto.HashGenerator;
import com.nexustree.core.diff.JsonDiffer;
import com.nexustree.core.diff.JsonPatcher;
import com.nexustree.data.entity.BranchEntity;
import com.nexustree.data.entity.CommitEntity;
import com.nexustree.data.entity.RepositoryEntity;
import com.nexustree.data.repository.BranchJpaRepository;
import com.nexustree.data.repository.CommitJpaRepository;
import com.nexustree.data.repository.RepositoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommitService {

    private final RepositoryJpaRepository repoRepository;
    private final BranchJpaRepository branchRepository;
    private final CommitJpaRepository commitRepository;

    private final JsonDiffer differ = new JsonDiffer();
    private final JsonPatcher patcher = new JsonPatcher();

    @Transactional
    public String createCommit(String repoName, String branchName, String message, JsonNode incomingJson) {

        RepositoryEntity repo = repoRepository.findByName(repoName)
                .orElseGet(() -> repoRepository.save(new RepositoryEntity(repoName)));

        BranchEntity branch = branchRepository.findByRepositoryAndName(repo, branchName)
                .orElseGet(() -> {
                    BranchEntity newBranch = new BranchEntity();
                    newBranch.setRepository(repo);
                    newBranch.setName(branchName);
                    newBranch.setHeadCommitHash(null);
                    return newBranch;
                });

        String parentHash = branch.getHeadCommitHash();
        JsonNode patchDataToSave;

        if (parentHash == null) {
            patchDataToSave = incomingJson;
        } else {
            JsonNode currentState = reconstructState(parentHash);
            patchDataToSave = differ.generateDiff(currentState, incomingJson);

            if (patchDataToSave.isEmpty()) {
                throw new IllegalArgumentException("No changes detected. Commit rejected.");
            }
        }

        String rawDataToHash = patchDataToSave.toString() + message + Instant.now().toString();
        String newHash = HashGenerator.generateSha256(rawDataToHash);

        CommitEntity commit = new CommitEntity();
        commit.setHash(newHash);
        commit.setRepository(repo);
        commit.setParentHash(parentHash);
        commit.setMessage(message);
        commit.setTimestamp(Instant.now());
        commit.setPatchData(patchDataToSave);
        commitRepository.save(commit);

        branch.setHeadCommitHash(newHash);
        branchRepository.save(branch);

        return newHash;
    }

    public JsonNode reconstructState(String commitHash) {
        List<CommitEntity> history = new ArrayList<>();
        String currentHash = commitHash;

        while (currentHash != null) {
            final String hashToSearch = currentHash;

            CommitEntity commit = commitRepository.findById(hashToSearch)
                    .orElseThrow(() -> new RuntimeException("Commit corrupted: " + hashToSearch));

            history.add(commit);
            currentHash = commit.getParentHash();
        }

        Collections.reverse(history);

        JsonNode state = history.get(0).getPatchData().deepCopy();

        for (int i = 1; i < history.size(); i++) {
            ArrayNode patches = (ArrayNode) history.get(i).getPatchData();
            state = patcher.applyPatch(state, patches);
        }

        return state;
    }
}
