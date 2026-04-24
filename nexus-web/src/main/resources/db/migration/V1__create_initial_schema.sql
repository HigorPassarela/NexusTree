CREATE TABLE repository (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE branch (
    id UUID PRIMARY KEY,
    repository_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    head_commit_hash VARCHAR(255) NOT NULL,
    CONSTRAINT fk_branch_repository FOREIGN KEY (repository_id) REFERENCES repository(id)
);

CREATE TABLE commit_log (
    hash VARCHAR(255) PRIMARY KEY,
    repository_id UUID NOT NULL,
    parent_hash VARCHAR(255),
    message VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    patch_data JSONB,
    CONSTRAINT fk_commit_repository FOREIGN KEY (repository_id) REFERENCES repository(id)
);

CREATE INDEX idx_branch_repo_name ON branch(repository_id, name);
CREATE INDEX idx_commit_parent ON commit_log(parent_hash);