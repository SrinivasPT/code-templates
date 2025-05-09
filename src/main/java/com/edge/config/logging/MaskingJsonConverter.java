package com.edge.config.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles JSON parsing and masking of sensitive fields in JSON content.
 */
public class MaskingJsonConverter {

    private final RedactionConfig redactionConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Pattern to find JSON objects in a string
    private static final Pattern JSON_PATTERN = Pattern.compile("\\{[^{}]*((\\{[^{}]*})[^{}]*)*}");

    /**
     * Constructor for backward compatibility
     */
    public MaskingJsonConverter(List<String> sensitiveFields, String maskValue) {
        this.redactionConfig = new RedactionConfig(maskValue);
        for (String field : sensitiveFields) {
            redactionConfig.addField(field);
        }
    }

    /**
     * Constructor using RedactionConfig
     */
    public MaskingJsonConverter(RedactionConfig redactionConfig) {
        this.redactionConfig = redactionConfig;
    }

    /**
     * Masks sensitive data in the given text that may contain JSON objects.
     */
    public String maskSensitiveData(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Find all potential JSON objects in the text
        Matcher matcher = JSON_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String potentialJson = matcher.group();
            String maskedJson = potentialJson;

            try {
                // Try to parse and mask JSON
                JsonNode jsonNode = objectMapper.readTree(potentialJson);
                JsonNode maskedNode = maskNode(jsonNode);
                maskedJson = objectMapper.writeValueAsString(maskedNode);
            } catch (Exception e) {
                // If parsing fails, keep the original text
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(maskedJson));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Recursively masks sensitive fields in a JsonNode.
     */
    private JsonNode maskNode(JsonNode node) {
        if (node.isObject()) {
            return maskObjectNode((ObjectNode) node);
        } else if (node.isArray()) {
            return maskArrayNode((ArrayNode) node);
        }
        return node;
    }

    /**
     * Masks sensitive fields in an ObjectNode.
     */
    private JsonNode maskObjectNode(ObjectNode objectNode) {
        ObjectNode result = objectNode.deepCopy();
        Iterator<Map.Entry<String, JsonNode>> fields = result.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode value = field.getValue();

            if (redactionConfig.shouldRedact(fieldName)) {
                if (value.isTextual()) {
                    // Apply the appropriate redaction strategy based on field name
                    String redactedValue = redactionConfig.redactValue(
                            fieldName, value.asText());
                    result.set(fieldName, new TextNode(redactedValue));
                } else {
                    // For non-text values, use the default redaction string
                    result.put(fieldName, redactionConfig.getRedactionString());
                }
            } else if (value.isObject() || value.isArray()) {
                // Recursively process nested objects and arrays
                result.set(fieldName, maskNode(value));
            }
        }

        return result;
    }

    /**
     * Masks sensitive fields in an ArrayNode.
     */
    private JsonNode maskArrayNode(ArrayNode arrayNode) {
        ArrayNode result = arrayNode.deepCopy();
        
        for (int i = 0; i < result.size(); i++) {
            JsonNode value = result.get(i);
            if (value.isObject() || value.isArray()) {
                result.set(i, maskNode(value));
            }
        }
        
        return result;
    }
}

