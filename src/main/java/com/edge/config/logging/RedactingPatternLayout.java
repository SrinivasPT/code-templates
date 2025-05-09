package com.edge.config.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Custom PatternLayout that redacts sensitive information in log messages.
 */
public class RedactingPatternLayout extends PatternLayout {
    
    private RedactionConfig redactionConfig;
    private boolean enableJsonRedaction = true;
    
    private static final Pattern SIMPLE_PATTERN = Pattern.compile("(?i)(\\b(?:%s)\\b\\s*[=:]\\s*[\"']?)([^\"'\\s,}{]+)");
    
    public RedactingPatternLayout() {
        this.redactionConfig = new RedactionConfig("********");
    }
    
    @Override
    public String doLayout(ILoggingEvent event) {
        String message = super.doLayout(event);
        
        // Skip redaction if no fields are configured or message is empty
        if (redactionConfig.getFieldNames().isEmpty() || !StringUtils.hasText(message)) {
            return message;
        }
        
        // Handle standard log messages with patterns like "password=123456" or "ssn: 123-45-6789"
        for (String field : redactionConfig.getFieldNames()) {
            String patternString = String.format(SIMPLE_PATTERN.pattern(), field);
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(message);
            
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String value = matcher.group(2);
                String redactedValue = redactionConfig.redactValue(field, value);
                matcher.appendReplacement(sb, matcher.group(1) + redactedValue);
            }
            matcher.appendTail(sb);
            message = sb.toString();
        }
        
        // Handle JSON redaction
        if (enableJsonRedaction && (message.contains("{") && message.contains("}"))) {
            try {
                message = new MaskingJsonConverter(redactionConfig).maskSensitiveData(message);
            } catch (Exception e) {
                // If JSON parsing fails, continue with the partially redacted message
            }
        }
        
        return message;
    }
    
    /**
     * Configure fields to redact with their default field type (FULL)
     */
    public void setFieldsToRedact(String fields) {
        if (StringUtils.hasText(fields)) {
            String[] fieldArray = fields.split("\\s*,\\s*");
            for (String field : fieldArray) {
                // Try to infer redaction type from field name
                addFieldWithInferredType(field);
            }
        }
    }
    
    /**
     * Add a field with inferred redaction type based on field name
     */
    private void addFieldWithInferredType(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return;
        }
        
        String lowerField = fieldName.toLowerCase();
        
        if (lowerField.contains("password") || lowerField.contains("secret") || lowerField.contains("token") || lowerField.contains("key")) {
            redactionConfig.addField(fieldName, RedactionType.FULL);
        } else if (lowerField.contains("credit") || lowerField.contains("card") || lowerField.contains("ccnum")) {
            redactionConfig.addField(fieldName, RedactionType.CREDIT_CARD);
        } else if (lowerField.contains("email")) {
            redactionConfig.addField(fieldName, RedactionType.EMAIL);
        } else if (lowerField.contains("phone") || lowerField.contains("mobile") || lowerField.contains("cell")) {
            redactionConfig.addField(fieldName, RedactionType.PHONE);
        } else if (lowerField.contains("ssn") || lowerField.contains("social") || lowerField.contains("security")) {
            redactionConfig.addField(fieldName, RedactionType.SSN);
        } else if (lowerField.contains("address")) {
            redactionConfig.addField(fieldName, RedactionType.ADDRESS);
        } else {
            // Default to full redaction for unrecognized fields
            redactionConfig.addField(fieldName, RedactionType.FULL);
        }
    }
    
    /**
     * Configure field types explicitly
     * Format: "fieldName=TYPE,fieldName2=TYPE2"
     * Example: "creditCard=CREDIT_CARD,email=EMAIL"
     */
    public void setFieldTypes(String fieldTypeConfig) {
        if (StringUtils.hasText(fieldTypeConfig)) {
            String[] fieldTypePairs = fieldTypeConfig.split("\\s*,\\s*");
            for (String pair : fieldTypePairs) {
                String[] parts = pair.split("\\s*=\\s*", 2);
                if (parts.length == 2) {
                    try {
                        RedactionType type = RedactionType.valueOf(parts[1].toUpperCase());
                        redactionConfig.addField(parts[0], type);
                    } catch (IllegalArgumentException e) {
                        // If type is invalid, default to FULL redaction
                        redactionConfig.addField(parts[0], RedactionType.FULL);
                    }
                }
            }
        }
    }
      public void setRedactionString(String redactionString) {
        if (StringUtils.hasText(redactionString)) {
            // Keep field configurations when creating a new RedactionConfig
            Map<String, RedactionType> oldFieldTypes = new HashMap<>();
            if (this.redactionConfig != null) {
                // Copy existing field configs
                for (String field : this.redactionConfig.getFieldNames()) {
                    oldFieldTypes.put(field, this.redactionConfig.getRedactionType(field));
                }
            }
            
            // Create new config with updated mask
            this.redactionConfig = new RedactionConfig(redactionString);
            
            // Restore previous field configurations
            for (Map.Entry<String, RedactionType> entry : oldFieldTypes.entrySet()) {
                this.redactionConfig.addField(entry.getKey(), entry.getValue());
            }
        }
    }
    
    public void setEnableJsonRedaction(boolean enableJsonRedaction) {
        this.enableJsonRedaction = enableJsonRedaction;
    }
}
