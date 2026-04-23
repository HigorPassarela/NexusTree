package com.nexustree.data.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "commit_log")
@Getter
@Setter
@NoArgsConstructor
public class CommitEntity {

    @Id
    private String hash;

    @ManyToOne(optional = false)
    @JoinColumn(name = "repository_id")
    private RepositoryEntity repository;

    @Column(name = "parent_hash")
    private String parentHash;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Instant timestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "patch_data", columnDefinition = "jsonb")
    private JsonNode patchData;
}
