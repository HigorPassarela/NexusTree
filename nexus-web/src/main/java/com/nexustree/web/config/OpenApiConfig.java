package com.nexustree.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NexusTree API - Data Versioning")
                        .version("1.0.0")
                        .description("Version Control Engine for Structured Data (JSON). \n\n" +
                                "This API acts like a 'Git' for data: it calculates the *Diff* between JSON payloads, " +
                                "generates SHA-256 Hashes, and allows time travel to reconstruct the exact state of a document at any given point in time.")
                        .contact(new Contact()
                                .name("Higor")
                                .email("higor@example.com")
                                .url("https://github.com/your-username"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
