package com.nexustree.data.repository;

import com.nexustree.data.entity.RepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RepositoryJpaRepository extends JpaRepository<RepositoryEntity, UUID> {
    Optional<RepositoryEntity> findByName(String name);
}
