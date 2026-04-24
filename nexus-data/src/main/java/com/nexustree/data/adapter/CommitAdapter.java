package com.nexustree.data.adapter;

import com.nexustree.core.domain.Commit;
import com.nexustree.core.port.CommitPort;
import com.nexustree.data.entity.CommitEntity;
import com.nexustree.data.entity.RepositoryEntity;
import com.nexustree.data.repository.CommitJpaRepository;
import com.nexustree.data.repository.RepositoryJpaRepository;
import com.nexustree.core.exception.CommitNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommitAdapter implements CommitPort {

    private final CommitJpaRepository commitRepository;
    private final RepositoryJpaRepository repoRepository;

    @Override
    public void save(Commit commit, String repoName) {
        RepositoryEntity repo = repoRepository.findByName(repoName)
                .orElseThrow(() -> new IllegalArgumentException("Repository not found!"));

        CommitEntity entity = new CommitEntity();
        entity.setHash(commit.hash());
        entity.setParentHash(commit.parentHash());
        entity.setMessage(commit.message());
        entity.setTimestamp(commit.timestamp());
        entity.setPatchData(commit.patchData());
        entity.setRepository(repo);

        commitRepository.save(entity);
    }

    @Override
    public Commit findByHash(String hash) {
        CommitEntity entity = commitRepository.findById(hash)
                .orElseThrow(() -> new CommitNotFoundException(hash));
        return toDomain(entity);
    }

    @Override
    public List<Commit> getHistoryChain(String headHash) {
        List<CommitEntity> history = new ArrayList<>();
        String currentHash = headHash;

        while (currentHash != null) {
            final String hashToSearch = currentHash;
            CommitEntity commitEntity = commitRepository.findById(hashToSearch)
                    .orElseThrow(() -> new CommitNotFoundException(hashToSearch));
            history.add(commitEntity);
            currentHash = commitEntity.getParentHash();
        }

        Collections.reverse(history);

        return history.stream().map(this::toDomain).toList();
    }

    private Commit toDomain(CommitEntity entity) {
        return new Commit(
                entity.getHash(),
                entity.getParentHash(),
                entity.getMessage(),
                entity.getTimestamp(),
                entity.getPatchData()
        );
    }
}
