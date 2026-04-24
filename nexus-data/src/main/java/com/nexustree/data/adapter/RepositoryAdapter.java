package com.nexustree.data.adapter;

import com.nexustree.core.port.RepositoryPort;
import com.nexustree.data.entity.RepositoryEntity;
import com.nexustree.data.repository.RepositoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RepositoryAdapter implements RepositoryPort {

    private final RepositoryJpaRepository jpaRepository;

    @Override
    public boolean ensureRepositoryExists(String repoName) {
        Optional<RepositoryEntity> existingRepo = jpaRepository.findByName(repoName);
        if (existingRepo.isEmpty()) {
            jpaRepository.save(new RepositoryEntity(repoName));
            return true;
        }
        return false;
    }
}
