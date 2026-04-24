package com.nexustree.web.config;

import com.nexustree.core.port.BranchPort;
import com.nexustree.core.port.CommitPort;
import com.nexustree.core.port.RepositoryPort;
import com.nexustree.core.service.CommitService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig {

    @Bean
    public CommitService commitService(RepositoryPort repositoryPort,
                                       BranchPort branchPort,
                                       CommitPort commitPort) {
        return new CommitService(repositoryPort, branchPort, commitPort);
    }
}
