package com.nexustree.core.diff;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonPatcher {

    public JsonNode applyPatch(JsonNode source, ArrayNode patches) {
        JsonNode result = source.deepCopy();

        for (JsonNode patch : patches) {
            String op = patch.get("op").asText();
            String path = patch.get("path").asText();

            String fieldName = path.substring(1);

            if (result.isObject()) {
                ObjectNode targetObj = (ObjectNode) result;

                switch (op) {
                    case "add":
                    case "replace":
                        JsonNode newValue = patch.get("value");
                        targetObj.set(fieldName, newValue);
                        break;

                    case "remove":
                        targetObj.remove(fieldName);
                        break;

                    default:
                        throw new IllegalArgumentException("Unknown patch operation: " + op);
                }
            }
        }

        return result;
    }
}
