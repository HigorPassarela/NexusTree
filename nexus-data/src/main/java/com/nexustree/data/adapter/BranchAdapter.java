package com.nexustree.data.adapter;

import com.nexustree.core.port.BranchPort;
import com.nexustree.data.entity.BranchEntity;
import com.nexustree.data.entity.RepositoryEntity;
import com.nexustree.data.repository.BranchJpaRepository;
import com.nexustree.data.repository.RepositoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BranchAdapter implements BranchPort {

    private final BranchJpaRepository branchRepository;
    private final RepositoryJpaRepository repoRepository;

    @Override
    public String getHeadCommitHash(String repoName, String branchName) {
        RepositoryEntity repo = repoRepository.findByName(repoName)
                .orElseThrow(() -> new IllegalArgumentException("Repository not found!"));

        return branchRepository.findByRepositoryAndName(repo, branchName)
                .map(BranchEntity::getHeadCommitHash)
                .orElse(null);
    }

    @Override
    public void updateHead(String repoName, String branchName, String newCommitHash) {
        RepositoryEntity repo = repoRepository.findByName(repoName)
                .orElseThrow(() -> new IllegalArgumentException("Repository not found!"));

        BranchEntity branch = branchRepository.findByRepositoryAndName(repo, branchName)
                .orElseGet(() -> {
                   BranchEntity newBranch = new BranchEntity();
                   newBranch.setRepository(repo);
                   newBranch.setName(branchName);
                   return newBranch;
                });

        branch.setHeadCommitHash(newCommitHash);
        branchRepository.save(branch);
    }
}
