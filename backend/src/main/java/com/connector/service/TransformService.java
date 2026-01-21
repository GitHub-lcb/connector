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

        // 1. Process Aggregation if exists (before mappings)
        if (config.containsKey("aggregation")) {
            Map<String, Object> aggregation = (Map<String, Object>) config.get("aggregation");
            if (aggregation != null && Boolean.TRUE.equals(aggregation.get("enabled"))) {
                result = processAggregation(result, aggregation);
            }
        }

        // 2. Process Mappings if exists
        if (config.containsKey("mappings")) {
            List<Map<String, Object>> mappings = (List<Map<String, Object>>) config.get("mappings");
            if (mappings != null && !mappings.isEmpty()) {
                result = processMappings(result, mappings);
            }
        }

        // 3. Process Security if exists
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

    /**
     * Process array aggregation
     * Aggregate array elements by grouping fields and sum/avg numeric fields
     */
    @SuppressWarnings("unchecked")
    private Object processAggregation(Object data, Map<String, Object> aggregationConfig) {
        String arrayField = (String) aggregationConfig.get("arrayField");
        List<String> groupByFields = (List<String>) aggregationConfig.get("groupByFields");
        List<String> keepFields = (List<String>) aggregationConfig.get("keepFields");
        List<String> sumFields = (List<String>) aggregationConfig.get("sumFields");
        List<String> avgFields = (List<String>) aggregationConfig.get("avgFields");
        String countField = (String) aggregationConfig.get("countField");

        if (arrayField == null || arrayField.isEmpty() || groupByFields == null || groupByFields.isEmpty()) {
            return data;
        }

        JsonNode rootNode = objectMapper.valueToTree(data);
        if (!rootNode.isObject()) {
            return data;
        }

        ObjectNode resultNode = rootNode.deepCopy();
        JsonNode arrayNode = getValue(resultNode, arrayField);
        
        if (arrayNode == null || !arrayNode.isArray()) {
            return data;
        }

        // Group array elements
        Map<String, List<JsonNode>> groups = new LinkedHashMap<>();
        
        for (JsonNode element : arrayNode) {
            if (!element.isObject()) continue;
            
            // Build group key from groupByFields
            StringBuilder keyBuilder = new StringBuilder();
            for (String field : groupByFields) {
                JsonNode fieldNode = getValue(element, field);
                String fieldValue = fieldNode != null ? fieldNode.asText() : "null";
                keyBuilder.append(fieldValue).append("|");
            }
            String groupKey = keyBuilder.toString();
            
            groups.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(element);
        }

        // Aggregate each group
        com.fasterxml.jackson.databind.node.ArrayNode aggregatedArray = objectMapper.createArrayNode();
        
        for (List<JsonNode> group : groups.values()) {
            if (group.isEmpty()) continue;
            
            ObjectNode aggregatedItem = objectMapper.createObjectNode();
            JsonNode firstItem = group.get(0);
            
            // 1. Set groupBy fields (from first item)
            for (String field : groupByFields) {
                JsonNode fieldNode = getValue(firstItem, field);
                if (fieldNode != null) {
                    setValueInNode(aggregatedItem, field, nodeToObject(fieldNode));
                }
            }
            
            // 2. Set keep fields (from first item)
            if (keepFields != null) {
                for (String field : keepFields) {
                    JsonNode fieldNode = getValue(firstItem, field);
                    if (fieldNode != null) {
                        setValueInNode(aggregatedItem, field, nodeToObject(fieldNode));
                    }
                }
            }
            
            // 3. Sum fields
            if (sumFields != null) {
                for (String field : sumFields) {
                    double sum = 0.0;
                    for (JsonNode item : group) {
                        JsonNode fieldNode = getValue(item, field);
                        if (fieldNode != null && fieldNode.isNumber()) {
                            sum += fieldNode.asDouble();
                        }
                    }
                    // Check if sum is integer
                    if (sum == Math.floor(sum)) {
                        setValueInNode(aggregatedItem, field, (long) sum);
                    } else {
                        setValueInNode(aggregatedItem, field, sum);
                    }
                }
            }
            
            // 4. Average fields
            if (avgFields != null) {
                for (String field : avgFields) {
                    double sum = 0.0;
                    int count = 0;
                    for (JsonNode item : group) {
                        JsonNode fieldNode = getValue(item, field);
                        if (fieldNode != null && fieldNode.isNumber()) {
                            sum += fieldNode.asDouble();
                            count++;
                        }
                    }
                    if (count > 0) {
                        double avg = sum / count;
                        setValueInNode(aggregatedItem, field, avg);
                    }
                }
            }
            
            // 5. Count field
            if (countField != null && !countField.isEmpty()) {
                setValueInNode(aggregatedItem, countField, group.size());
            }
            
            aggregatedArray.add(aggregatedItem);
        }

        // Replace original array with aggregated array
        setValue(resultNode, arrayField, objectMapper.convertValue(aggregatedArray, Object.class));

        return objectMapper.convertValue(resultNode, Object.class);
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

            // Check if this is an array mapping
            if (sourcePath.contains("[]")) {
                processArrayMapping(rootNode, resultNode, sourcePath, targetPath, defaultValue, transformations, condition);
                continue;
            }

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

    /**
     * Process array field mappings like detail[].quantity -> detail[].qty
     */
    @SuppressWarnings("unchecked")
    private void processArrayMapping(JsonNode rootNode, ObjectNode resultNode, 
                                      String sourcePath, String targetPath,
                                      Object defaultValue,
                                      List<Map<String, Object>> transformations,
                                      Map<String, Object> condition) {
        // Parse array path: detail[].quantity
        String[] sourceParts = sourcePath.split("\\[\\]");
        String[] targetParts = targetPath.split("\\[\\]");
        
        if (sourceParts.length != 2 || targetParts.length != 2) {
            // Invalid array path format
            return;
        }
        
        String sourceArrayPath = sourceParts[0]; // "detail"
        String sourceFieldPath = sourceParts[1].startsWith(".") ? sourceParts[1].substring(1) : sourceParts[1]; // "quantity"
        
        String targetArrayPath = targetParts[0];
        String targetFieldPath = targetParts[1].startsWith(".") ? targetParts[1].substring(1) : targetParts[1];
        
        // Get source array
        JsonNode sourceArrayNode = getValue(rootNode, sourceArrayPath);
        if (sourceArrayNode == null || !sourceArrayNode.isArray()) {
            return;
        }
        
        // Ensure target array exists in result
        JsonNode targetArrayNode = getValue(resultNode, targetArrayPath);
        if (targetArrayNode == null || !targetArrayNode.isArray()) {
            // Create empty array node first
            com.fasterxml.jackson.databind.node.ArrayNode newArrayNode = objectMapper.createArrayNode();
            setValue(resultNode, targetArrayPath, objectMapper.convertValue(newArrayNode, Object.class));
        }
        
        // Process each array element
        for (int i = 0; i < sourceArrayNode.size(); i++) {
            JsonNode sourceElement = sourceArrayNode.get(i);
            if (!sourceElement.isObject()) continue;
            
            // Get field value from source element
            JsonNode fieldNode = sourceFieldPath.isEmpty() ? sourceElement : getValue(sourceElement, sourceFieldPath);
            Object value = nodeToObject(fieldNode);
            
            // Condition check
            if (condition != null && !checkCondition(value, condition)) {
                continue;
            }
            
            // Default value
            if (value == null && defaultValue != null) {
                value = defaultValue;
            }
            
            // Transformations
            if (value != null && transformations != null) {
                for (Map<String, Object> step : transformations) {
                    value = applyTransformation(value, step);
                }
            }
            
            if (value != null) {
                // Get target array
                JsonNode targetArrayNodeFinal = getValue(resultNode, targetArrayPath);
                if (targetArrayNodeFinal != null && targetArrayNodeFinal.isArray()) {
                    com.fasterxml.jackson.databind.node.ArrayNode targetArray = (com.fasterxml.jackson.databind.node.ArrayNode) targetArrayNodeFinal;
                    
                    // Ensure target array has element at index i
                    while (targetArray.size() <= i) {
                        targetArray.add(objectMapper.createObjectNode());
                    }
                    
                    // Get or create target element
                    JsonNode targetElement = targetArray.get(i);
                    if (targetElement != null && targetElement.isObject()) {
                        ObjectNode targetObj = (ObjectNode) targetElement;
                        if (targetFieldPath.isEmpty()) {
                            // Can't set root of array element, skip
                        } else {
                            setValueInNode(targetObj, targetFieldPath, value);
                        }
                    }
                }
            }
        }
    }
    
    private void setValueInNode(ObjectNode node, String path, Object value) {
        if (path.isEmpty()) return;
        
        String[] parts = path.split("\\.");
        ObjectNode current = node;
        
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            JsonNode childNode = current.get(part);
            if (childNode == null || !childNode.isObject()) {
                childNode = objectMapper.createObjectNode();
                current.set(part, childNode);
            }
            current = (ObjectNode) childNode;
        }
        
        current.set(parts[parts.length - 1], objectMapper.valueToTree(value));
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
        // Handle array notation: detail[].quantity
        if (path.contains("[]")) {
            // Not supported for now - arrays require special handling
            // For array element access, we'd need to process all array elements
            return null;
        }
        
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
