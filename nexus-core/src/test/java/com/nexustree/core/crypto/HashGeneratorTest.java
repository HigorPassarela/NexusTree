package com.nexustree.core.crypto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class HashGeneratorTest {

    @Test
    void shouldGenerateConsistentSha256Hash() {
        String input = "O rato roeu a roupa do rei de roma";

        String hash1 = HashGenerator.generateSha256(input);
        String hash2 = HashGenerator.generateSha256(input);

        System.out.println("Hash gerado: " + hash1);

        assertThat(hash1).hasSize(64);

        assertThat(hash1).isEqualTo(hash2);

        assertThat(hash1).isNotEqualTo(input);
    }
}