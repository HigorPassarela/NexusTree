package com.nexustree.core.port;

import com.nexustree.core.domain.Commit;

import java.util.List;

public interface CommitPort {
    void save(Commit commit, String repoName);

    Commit findByHash(String hash);

    List<Commit> getHistoryChain(String headHash);
}
