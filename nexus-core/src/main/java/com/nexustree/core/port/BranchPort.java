package com.nexustree.core.port;

public interface BranchPort {
    String getHeadCommitHash(String repoName, String branchName);

    void updateHead(String repoName, String branchName, String newCommitHash);
}
