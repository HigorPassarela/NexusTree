package com.nexustree.core.diff;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonDifferTest {

    private JsonDiffer differ;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        differ = new JsonDiffer();
        mapper = new ObjectMapper();
    }

    @Test
    void shouldGenerateCorrectPatchesForAddChangeAndRemove() throws Exception {
        String sourceJson = """
                {
                  "name": "Jose",
                  "role": "Junior",
                  "active": true
                }
                """;

        String targetJson = """
                {
                  "name": "Jose",
                  "role": "Senior",
                  "level": 99
                }
                """;

        JsonNode sourceNode = mapper.readTree(sourceJson);
        JsonNode targetNode = mapper.readTree(targetJson);

        ArrayNode diffs = differ.generateDiff(sourceNode, targetNode);

        System.out.println("Patches Gerados: \n" + diffs.toPrettyString());

        assertThat(diffs).hasSize(3);

        String diffString = diffs.toString();
        assertThat(diffString).contains("\"op\":\"replace\",\"path\":\"/role\",\"value\":\"Senior\"");
        assertThat(diffString).contains("\"op\":\"add\",\"path\":\"/level\",\"value\":99");
        assertThat(diffString).contains("\"op\":\"remove\",\"path\":\"/active\"");
    }
}