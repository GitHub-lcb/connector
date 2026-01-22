package net.lab1024.sa.admin.module.business.connector.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.lab1024.sa.admin.module.business.connector.util.RsaUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 转换服务
 */
@Service
public class ConnectorTransformService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public Object transform(Object data, Map<String, Object> config) {
        if (config == null) {
            return data;
        }

        Object result = data;

        // 1. 处理聚合配置（在映射之前）
        if (config.containsKey("aggregation")) {
            Map<String, Object> aggregation = (Map<String, Object>) config.get("aggregation");
            if (aggregation != null && Boolean.TRUE.equals(aggregation.get("enabled"))) {
                result = processAggregation(result, aggregation);
            }
        }

        // 2. 处理字段映射配置
        if (config.containsKey("mappings")) {
            List<Map<String, Object>> mappings = (List<Map<String, Object>>) config.get("mappings");
            if (mappings != null && !mappings.isEmpty()) {
                result = processMappings(result, mappings);
            }
        }

        // 3. 处理安全加密配置
        if (config.containsKey("security")) {
            Map<String, Object> security = (Map<String, Object>) config.get("security");
            result = processSecurity(result, security);
        }

        return result;
    }

    // 此方法被ProxyController调用以获取自定义请求头
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
     * 处理数组聚合
     * 根据分组字段聚合数组元素，并对数值字段进行求和/求平均值
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

        // 对数组元素进行分组
        Map<String, List<JsonNode>> groups = new LinkedHashMap<>();
        
        for (JsonNode element : arrayNode) {
            if (!element.isObject()) continue;
            
            // 根据分组字段构建分组键
            StringBuilder keyBuilder = new StringBuilder();
            for (String field : groupByFields) {
                JsonNode fieldNode = getValue(element, field);
                String fieldValue = fieldNode != null ? fieldNode.asText() : "null";
                keyBuilder.append(fieldValue).append("|");
            }
            String groupKey = keyBuilder.toString();
            
            groups.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(element);
        }

        // 对每个分组进行聚合
        com.fasterxml.jackson.databind.node.ArrayNode aggregatedArray = objectMapper.createArrayNode();
        
        for (List<JsonNode> group : groups.values()) {
            if (group.isEmpty()) continue;
            
            ObjectNode firstItem = (ObjectNode) group.get(0);
            ObjectNode aggregatedItem = firstItem.deepCopy();
            
            // 1. 设置分组字段（从第一项获取）- 由于已深拷贝firstItem，此步骤冗余
            // 2. 设置保留字段（从第一项获取）- 由于已深拷贝firstItem，此步骤冗余
            
            // 3. 对求和字段进行处理
            if (sumFields != null) {
                for (String field : sumFields) {
                    java.math.BigDecimal sum = java.math.BigDecimal.ZERO;
                    for (JsonNode item : group) {
                        JsonNode fieldNode = getValue(item, field);
                        if (fieldNode != null && fieldNode.isNumber()) {
                            sum = sum.add(new java.math.BigDecimal(fieldNode.asText()));
                        }
                    }
                    // 保留3位小数
                    setValueInNode(aggregatedItem, field, sum.setScale(3, java.math.RoundingMode.HALF_UP));
                }
            }
            
            // 4. 对平均值字段进行处理
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
            
            // 5. 设置计数字段
            if (countField != null && !countField.isEmpty()) {
                setValueInNode(aggregatedItem, countField, group.size());
            }
            
            aggregatedArray.add(aggregatedItem);
        }

        // 用聚合后的数组替换原始数组
        setValue(resultNode, arrayField, objectMapper.convertValue(aggregatedArray, Object.class));

        return objectMapper.convertValue(resultNode, Object.class);
    }

    @SuppressWarnings("unchecked")
    private Object processMappings(Object data, List<Map<String, Object>> mappings) {
        JsonNode rootNode = objectMapper.valueToTree(data);
        if (!rootNode.isObject()) {
            return data;
        }
        
        ObjectNode resultNode = rootNode.deepCopy();

        for (Map<String, Object> rule : mappings) {
            String sourcePath = (String) rule.get("source");
            String targetPath = (String) rule.get("target");
            Object defaultValue = rule.get("defaultValue");
            List<Map<String, Object>> transformations = (List<Map<String, Object>>) rule.get("transformations");
            Map<String, Object> condition = (Map<String, Object>) rule.get("condition");

            // 检查是否为数组映射
            if (sourcePath.contains("[]")) {
                processArrayMapping(rootNode, resultNode, sourcePath, targetPath, defaultValue, transformations, condition);
                continue;
            }

            // 1. 获取源字段值
            JsonNode valueNode = getValue(rootNode, sourcePath);
            Object value = nodeToObject(valueNode);

            // 2. 条件检查
            if (condition != null && !checkCondition(value, condition)) {
                continue;
            }

            // 3. 设置默认值
            if (value == null && defaultValue != null) {
                value = defaultValue;
            }

            // 4. 执行转换操作
            if (value != null && transformations != null) {
                for (Map<String, Object> step : transformations) {
                    value = applyTransformation(value, step);
                }
            }

            // 5. 设置目标字段值
            if (value != null) {
                setValue(resultNode, targetPath, value);

                // 6. 清理源字段
                if (!sourcePath.equals(targetPath)) {
                    unsetValue(resultNode, sourcePath);
                }
            }
        }

        return objectMapper.convertValue(resultNode, Object.class);
    }

    /**
     * 处理数组字段映射，如 detail[].quantity -> detail[].qty
     */
    @SuppressWarnings("unchecked")
    private void processArrayMapping(JsonNode rootNode, ObjectNode resultNode, 
                                      String sourcePath, String targetPath,
                                      Object defaultValue,
                                      List<Map<String, Object>> transformations,
                                      Map<String, Object> condition) {
        // 解析数组路径：detail[].quantity
        String[] sourceParts = sourcePath.split("\\[\\]");
        String[] targetParts = targetPath.split("\\[\\]");
        
        if (sourceParts.length != 2 || targetParts.length != 2) {
            // 数组路径格式无效
            return;
        }
        
        String sourceArrayPath = sourceParts[0]; // 例如："detail"
        String sourceFieldPath = sourceParts[1].startsWith(".") ? sourceParts[1].substring(1) : sourceParts[1]; // 例如："quantity"
        
        String targetArrayPath = targetParts[0];
        String targetFieldPath = targetParts[1].startsWith(".") ? targetParts[1].substring(1) : targetParts[1];
        
        // 获取源数组
        JsonNode sourceArrayNode = getValue(rootNode, sourceArrayPath);
        if (sourceArrayNode == null || !sourceArrayNode.isArray()) {
            return;
        }
        
        // 确保结果中存在目标数组
        JsonNode targetArrayNode = getValue(resultNode, targetArrayPath);
        if (targetArrayNode == null || !targetArrayNode.isArray()) {
            // 首先创建空数组节点
            com.fasterxml.jackson.databind.node.ArrayNode newArrayNode = objectMapper.createArrayNode();
            setValue(resultNode, targetArrayPath, objectMapper.convertValue(newArrayNode, Object.class));
        }
        
        // 处理每个数组元素
        for (int i = 0; i < sourceArrayNode.size(); i++) {
            JsonNode sourceElement = sourceArrayNode.get(i);
            if (!sourceElement.isObject()) continue;
            
            // 从源元素中获取字段值
            JsonNode fieldNode = sourceFieldPath.isEmpty() ? sourceElement : getValue(sourceElement, sourceFieldPath);
            Object value = nodeToObject(fieldNode);
            
            // 条件检查
            if (condition != null && !checkCondition(value, condition)) {
                continue;
            }
            
            // 设置默认值
            if (value == null && defaultValue != null) {
                value = defaultValue;
            }
            
            // 执行转换操作
            if (value != null && transformations != null) {
                for (Map<String, Object> step : transformations) {
                    value = applyTransformation(value, step);
                }
            }
            
            if (value != null) {
                // 获取目标数组
                JsonNode targetArrayNodeFinal = getValue(resultNode, targetArrayPath);
                if (targetArrayNodeFinal != null && targetArrayNodeFinal.isArray()) {
                    com.fasterxml.jackson.databind.node.ArrayNode targetArray = (com.fasterxml.jackson.databind.node.ArrayNode) targetArrayNodeFinal;
                    
                    // 确保目标数组在索引i处有元素
                    while (targetArray.size() <= i) {
                        targetArray.add(objectMapper.createObjectNode());
                    }
                    
                    // 获取或创建目标元素
                    JsonNode targetElement = targetArray.get(i);
                    if (targetElement != null && targetElement.isObject()) {
                        ObjectNode targetObj = (ObjectNode) targetElement;
                        if (targetFieldPath.isEmpty()) {
                            // 无法设置数组元素的根，跳过
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
            // 将当前数据转换为JSON字符串
            String jsonContent = objectMapper.writeValueAsString(data);
            
            // 执行RSA加密
            String encrypted = RsaUtils.encrypt(jsonContent, publicKey);
            
            if (encryptedField != null && !encryptedField.isEmpty()) {
                Map<String, String> wrapper = new HashMap<>();
                wrapper.put(encryptedField, encrypted);
                return wrapper;
            } else {
                Map<String, String> wrapper = new HashMap<>();
                wrapper.put("data", encrypted);
                return wrapper;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Encryption failed: " + e.getMessage());
        }
    }

    private JsonNode getValue(JsonNode root, String path) {
        // 处理数组表示法：detail[].quantity
        if (path.contains("[]")) {
            // 暂不支持 - 数组需要特殊处理
            // 对于数组元素访问，我们需要处理所有数组元素
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
