package com.nexustree.data.repository;

import com.nexustree.data.entity.BranchEntity;
import com.nexustree.data.entity.RepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BranchJpaRepository extends JpaRepository<BranchEntity, UUID> {
    Optional<BranchEntity> findByRepositoryAndName(RepositoryEntity repository, String name);
}
