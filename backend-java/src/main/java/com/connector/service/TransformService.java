package com.connector.service;

import com.connector.util.RsaUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class TransformService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public Object transform(Object data, Map<String, Object> config) {
        if (config == null) {
            return data;
        }

        Object result = data;

        // 1. Process Mappings if exists
        if (config.containsKey("mappings")) {
            List<Map<String, Object>> mappings = (List<Map<String, Object>>) config.get("mappings");
            if (mappings != null && !mappings.isEmpty()) {
                result = processMappings(data, mappings);
            }
        }

        // 2. Process Security if exists
        if (config.containsKey("security")) {
            Map<String, Object> security = (Map<String, Object>) config.get("security");
            result = processSecurity(result, security);
        }

        return result;
    }

    // This method is called by ProxyController to get custom headers
    @SuppressWarnings("unchecked")
    public Map<String, String> getCustomHeaders(Map<String, Object> config) {
        if (config == null || !config.containsKey("headers")) {
            return Collections.emptyMap();
        }
        
        List<Map<String, String>> headerConfigs = (List<Map<String, String>>) config.get("headers");
        if (headerConfigs == null || headerConfigs.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Map<String, String> headers = new HashMap<>();
        for (Map<String, String> h : headerConfigs) {
            String key = h.get("key");
            String value = h.get("value");
            if (key != null && !key.isEmpty()) {
                headers.put(key, value);
            }
        }
        return headers;
    }

    @SuppressWarnings("unchecked")
    private Object processMappings(Object data, List<Map<String, Object>> mappings) {
        // Deep clone data to avoid mutation if possible, or just start with new structure?
        // Node.js implementation clones deeply.
        // For simplicity with Jackson, we can convert to JsonNode, modify, and convert back.
        JsonNode rootNode = objectMapper.valueToTree(data);
        if (!rootNode.isObject()) {
            return data;
        }
        
        // We will work on a deep copy or modify a copy
        ObjectNode resultNode = rootNode.deepCopy();

        for (Map<String, Object> rule : mappings) {
            String sourcePath = (String) rule.get("source");
            String targetPath = (String) rule.get("target");
            Object defaultValue = rule.get("defaultValue");
            List<Map<String, Object>> transformations = (List<Map<String, Object>>) rule.get("transformations");
            Map<String, Object> condition = (Map<String, Object>) rule.get("condition");

            // 1. Get Value
            JsonNode valueNode = getValue(rootNode, sourcePath);
            Object value = nodeToObject(valueNode);

            // 2. Condition Check
            if (condition != null && !checkCondition(value, condition)) {
                continue;
            }

            // 3. Default Value
            if (value == null && defaultValue != null) {
                value = defaultValue;
            }

            // 4. Transformations
            if (value != null && transformations != null) {
                for (Map<String, Object> step : transformations) {
                    value = applyTransformation(value, step);
                }
            }

            // 5. Set Target
            if (value != null) {
                setValue(resultNode, targetPath, value);

                // 6. Cleanup Source
                if (!sourcePath.equals(targetPath)) {
                    unsetValue(resultNode, sourcePath);
                }
            }
        }

        return objectMapper.convertValue(resultNode, Object.class);
    }

    private Object processSecurity(Object data, Map<String, Object> security) {
        String type = (String) security.get("type");
        if (!"RSA".equalsIgnoreCase(type)) {
            return data;
        }

        String publicKey = (String) security.get("publicKey");
        String encryptedField = (String) security.get("encryptedField");

        if (publicKey == null || publicKey.isEmpty()) {
            return data;
        }

        try {
            // Convert current data to JSON string
            String jsonContent = objectMapper.writeValueAsString(data);
            
            // Encrypt
            String encrypted = RsaUtils.encrypt(jsonContent, publicKey);

            // If encryptedField is specified, wrap it in an object. Otherwise return the encrypted string (or wrap in default?)
            // Usually API expects JSON, so returning raw string might be invalid if content-type is json.
            // Let's assume if encryptedField is provided, we return { "field": "encrypted..." }
            // If not provided, we might return just the string? Or maybe "data" is default?
            
            if (encryptedField != null && !encryptedField.isEmpty()) {
                Map<String, String> wrapper = new HashMap<>();
                wrapper.put(encryptedField, encrypted);
                return wrapper;
            } else {
                // If no field specified, maybe return a map with "data"? Or just return the string?
                // Returning string might break downstream if they expect JSON object. 
                // Let's wrap in "data" by default if field is missing but it's an object?
                // Or user intends to send raw body.
                // For safety, let's default to "data" if not specified to keep it valid JSON object.
                Map<String, String> wrapper = new HashMap<>();
                wrapper.put("data", encrypted);
                return wrapper;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // In case of error, return original data or throw?
            // Let's return original with error logged (in real world, should throw)
            throw new RuntimeException("Encryption failed: " + e.getMessage());
        }
    }

    private JsonNode getValue(JsonNode root, String path) {
        String[] parts = path.split("\\.");
        JsonNode current = root;
        for (String part : parts) {
            if (current == null || !current.isObject()) return null;
            current = current.get(part);
        }
        return current;
    }

    private void setValue(ObjectNode root, String path, Object value) {
        String[] parts = path.split("\\.");
        ObjectNode current = root;
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            JsonNode node = current.get(part);
            if (node == null || !node.isObject()) {
                node = objectMapper.createObjectNode();
                current.set(part, node);
            }
            current = (ObjectNode) node;
        }
        current.set(parts[parts.length - 1], objectMapper.valueToTree(value));
    }

    private void unsetValue(ObjectNode root, String path) {
        String[] parts = path.split("\\.");
        ObjectNode current = root;
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            JsonNode node = current.get(part);
            if (node == null || !node.isObject()) return;
            current = (ObjectNode) node;
        }
        current.remove(parts[parts.length - 1]);
    }

    private Object nodeToObject(JsonNode node) {
        if (node == null || node.isNull()) return null;
        if (node.isTextual()) return node.asText();
        if (node.isInt()) return node.asInt();
        if (node.isLong()) return node.asLong();
        if (node.isDouble()) return node.asDouble();
        if (node.isBoolean()) return node.asBoolean();
        if (node.isArray() || node.isObject()) return objectMapper.convertValue(node, Object.class);
        return node.asText();
    }

    private boolean checkCondition(Object value, Map<String, Object> condition) {
        String operator = (String) condition.get("operator");
        Object condValue = condition.get("value");

        switch (operator) {
            case "exists": return value != null;
            case "not_exists": return value == null;
            case "equals": return Objects.equals(value, condValue);
            case "not_equals": return !Objects.equals(value, condValue);
            case "contains": return String.valueOf(value).contains(String.valueOf(condValue));
            case "gt": return compare(value, condValue) > 0;
            case "lt": return compare(value, condValue) < 0;
            default: return true;
        }
    }

    private int compare(Object v1, Object v2) {
        try {
            double d1 = Double.parseDouble(String.valueOf(v1));
            double d2 = Double.parseDouble(String.valueOf(v2));
            return Double.compare(d1, d2);
        } catch (Exception e) {
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private Object applyTransformation(Object value, Map<String, Object> step) {
        String type = (String) step.get("type");
        List<Object> params = (List<Object>) step.get("params");
        if (params == null) params = Collections.emptyList();

        String strVal = String.valueOf(value);

        try {
            switch (type) {
                case "uppercase": return strVal.toUpperCase();
                case "lowercase": return strVal.toLowerCase();
                case "trim": return strVal.trim();
                case "substring":
                    int start = getInt(params, 0);
                    int end = getInt(params, 1);
                    return strVal.substring(start, end);
                case "concat":
                    return strVal + params.get(0);
                case "replace":
                    String p1 = (String) params.get(0);
                    String p2 = params.size() > 1 ? (String) params.get(1) : "";
                    // Simple logic for regex if looks like regex, else literal
                    // Java regex doesn't use /flags syntax directly
                    return strVal.replaceAll(p1, p2); 
                case "split":
                    return Arrays.asList(strVal.split((String) params.get(0)));
                case "join":
                    if (value instanceof List) {
                        return String.join((String) params.get(0), (List<String>) value);
                    }
                    return value;
                case "base64_encode":
                    return Base64.getEncoder().encodeToString(strVal.getBytes(StandardCharsets.UTF_8));
                case "base64_decode":
                    return new String(Base64.getDecoder().decode(strVal), StandardCharsets.UTF_8);
                case "number":
                    return Double.parseDouble(strVal);
                case "string":
                    return strVal;
                case "boolean":
                    return Boolean.parseBoolean(strVal);
                case "json_parse":
                    return objectMapper.readValue(strVal, Object.class);
                case "json_stringify":
                    return objectMapper.writeValueAsString(value);
                default:
                    return value;
            }
        } catch (Exception e) {
            return value;
        }
    }

    private int getInt(List<Object> params, int index) {
        if (params.size() <= index) return 0;
        return Integer.parseInt(String.valueOf(params.get(index)));
    }
}
