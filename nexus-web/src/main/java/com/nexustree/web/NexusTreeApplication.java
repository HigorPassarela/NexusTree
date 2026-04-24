package com.nexustree.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.nexustree.web", "com.nexustree.data"})
@EntityScan(basePackages = "com.nexustree.data.entity")
@EnableJpaRepositories(basePackages = "com.nexustree.data.repository")
public class NexusTreeApplication {
    public static void main(String[] args) {
        SpringApplication.run(NexusTreeApplication.class, args);
    }
}
