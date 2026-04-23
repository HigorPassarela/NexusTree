package com.nexustree.data.repository;

import com.nexustree.data.entity.CommitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitJpaRepository extends JpaRepository<CommitEntity, String> {
}
