package com.nexustree.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.nexustree.data.entity")
@EnableJpaRepositories(basePackages = "com.nexustree.data.repository")
public class NexusTreeApplication {
    public static void main(String[] args) {
        SpringApplication.run(NexusTreeApplication.class, args);
    }
}
