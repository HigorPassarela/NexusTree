plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":nexus-core"))
    implementation(project(":nexus-data"))

    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")

    runtimeOnly("com.h2database:h2")

    implementation("org.flywaydb:flyway-core")
}