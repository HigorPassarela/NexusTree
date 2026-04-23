package com.nexustree.core.diff;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;

public class JsonDiffer {

    private static final ObjectMapper mapper = new ObjectMapper();

    public ArrayNode generateDiff(JsonNode source, JsonNode target) {
        ArrayNode patches = mapper.createArrayNode();
        compare("", source, target, patches);
        return patches;
    }

    private void compare(String path, JsonNode source, JsonNode target, ArrayNode patches) {
        if (source.equals(target)) {
            return;
        }

        if (source.isObject() && target.isObject()) {
            ObjectNode srcObj = (ObjectNode) source;
            ObjectNode tgtObj = (ObjectNode) target;

            Iterator<String> targetKeys = tgtObj.fieldNames();
            while (targetKeys.hasNext()) {
                String key = targetKeys.next();
                String newPath = path + "/" + key;

                if (!srcObj.has(key)) {
                    addPatchOperation(patches, "add", newPath, tgtObj.get(key));
                } else {
                    compare(newPath, srcObj.get(key), tgtObj.get(key), patches);
                }
            }

            Iterator<String> sourceKeys = srcObj.fieldNames();
            while (sourceKeys.hasNext()) {
                String key = sourceKeys.next();
                if (!tgtObj.has(key)) {
                    addPatchOperation(patches, "remove", path + "/" + key, null);
                }
            }
        } else {
            String finalPath = path.isEmpty() ? "/" : path;
            addPatchOperation(patches, "replace", finalPath, target);
        }
    }

    private void addPatchOperation(ArrayNode patches, String op, String path, JsonNode value) {
        ObjectNode patch = patches.addObject();
        patch.put("op", op);
        patch.put("path", path);
        if (value != null) {
            patch.set("value", value);
        }
    }
}
