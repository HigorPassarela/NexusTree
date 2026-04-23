package com.nexustree.core.diff;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonPatcherTest {

    private JsonDiffer differ;
    private JsonPatcher patcher;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        differ = new JsonDiffer();
        patcher = new JsonPatcher();
        mapper = new ObjectMapper();
    }

    @Test
    void shouldReconstructTargetJsonUsingSourceAndPatches() throws Exception {
        String v1Json = """
                {
                  "name": "Higor",
                  "status": "Learning",
                  "score": 10
                }
                """;

        String v2Json = """
                {
                  "name": "Higor",
                  "status": "Mastering",
                  "certified": true
                }
                """;

        JsonNode v1Node = mapper.readTree(v1Json);
        JsonNode v2Node = mapper.readTree(v2Json);

        ArrayNode diffs = differ.generateDiff(v1Node, v2Node);

        System.out.println("--- Diffs Salvos no Banco ---");
        System.out.println(diffs.toPrettyString());

        JsonNode reconstructedV2 = patcher.applyPatch(v1Node, diffs);

        System.out.println("\n--- JSON Reconstruído ---");
        System.out.println(reconstructedV2.toPrettyString());

        assertThat(reconstructedV2).isEqualTo(v2Node);

        assertThat(reconstructedV2.get("status").asText()).isEqualTo("Mastering");
        assertThat(reconstructedV2.has("score")).isFalse();
        assertThat(reconstructedV2.get("certified").asBoolean()).isTrue();
    }
}