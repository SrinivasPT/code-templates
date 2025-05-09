package com.edge.config.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Configuration for field redaction based on field name.
 */
public class RedactionConfig {
    private final Map<String, RedactionType> fieldRedactionMap;
    private final String redactionString;
    
    public RedactionConfig(String redactionString) {
        this.fieldRedactionMap = new HashMap<>();
        this.redactionString = redactionString;
    }
    
    /**
     * Add a field with full redaction
     * @param fieldName The field name to redact
     */
    public void addField(String fieldName) {
        fieldRedactionMap.put(fieldName.toLowerCase(), RedactionType.FULL);
    }
    
    /**
     * Add a field with a specific redaction type
     * @param fieldName The field name to redact
     * @param redactionType The type of redaction to apply
     */
    public void addField(String fieldName, RedactionType redactionType) {
        fieldRedactionMap.put(fieldName.toLowerCase(), redactionType);
    }
    
    /**
     * Get the redaction type for a field
     * @param fieldName The field name
     * @return The redaction type, or null if the field should not be redacted
     */
    public RedactionType getRedactionType(String fieldName) {
        return fieldRedactionMap.get(fieldName.toLowerCase());
    }
    
    /**
     * Check if a field should be redacted
     * @param fieldName The field name
     * @return true if the field should be redacted, false otherwise
     */
    public boolean shouldRedact(String fieldName) {
        return fieldRedactionMap.containsKey(fieldName.toLowerCase());
    }
    
    /**
     * Get all field names that should be redacted
     * @return Set of field names
     */
    public Set<String> getFieldNames() {
        return fieldRedactionMap.keySet();
    }
    
    /**
     * Get the redaction string (mask)
     * @return The redaction string
     */
    public String getRedactionString() {
        return redactionString;
    }
    
    /**
     * Apply redaction to a value based on the field name
     * @param fieldName The field name
     * @param value The value to potentially redact
     * @return The redacted value
     */
    public String redactValue(String fieldName, String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        RedactionType type = getRedactionType(fieldName);
        if (type == null) {
            return value;
        }
        
        switch (type) {
            case FULL:
                return redactionString;
                
            case CREDIT_CARD:
                return redactCreditCard(value);
                
            case EMAIL:
                return redactEmail(value);
                
            case PHONE:
                return redactPhone(value);
                
            case SSN:
                return redactSSN(value);
                
            case ADDRESS:
                return redactAddress(value);
                
            default:
                return redactionString;
        }
    }
    
    /**
     * Redact a credit card number, showing only last 4 digits
     */
    private String redactCreditCard(String value) {
        String cleaned = value.replaceAll("[^0-9]", "");
        if (cleaned.length() < 4) {
            return redactionString;
        }
        return "**** **** **** " + cleaned.substring(cleaned.length() - 4);
    }
      /**
     * Redact an email address, showing first character and domain
     */
    private String redactEmail(String value) {
        if (!value.contains("@")) {
            return redactionString;
        }
        
        String[] parts = value.split("@", 2);
        if (parts[0].isEmpty()) {
            return redactionString + "@" + parts[1];
        }
        
        return parts[0].substring(0, 1) + redactionString + "@" + parts[1];
    }
    
    /**
     * Redact a phone number, showing only last 4 digits
     */
    private String redactPhone(String value) {
        String cleaned = value.replaceAll("[^0-9]", "");
        if (cleaned.length() < 4) {
            return redactionString;
        }
        return "******" + cleaned.substring(cleaned.length() - 4);
    }
    
    /**
     * Redact a SSN/government ID, showing only last 4 digits
     */
    private String redactSSN(String value) {
        String cleaned = value.replaceAll("[^0-9]", "");
        if (cleaned.length() < 4) {
            return redactionString;
        }
        return "***-**-" + cleaned.substring(cleaned.length() - 4);
    }
    
    /**
     * Redact an address, showing only city and country if available
     */
    private String redactAddress(String value) {
        // Attempt to extract city and country from address
        // This is a simple implementation that assumes commas separate address parts
        String[] parts = value.split(",");
        if (parts.length <= 1) {
            return redactionString;
        }
        
        // Return only the last two parts (usually city and country)
        StringBuilder redacted = new StringBuilder();
        redacted.append("*****");
        for (int i = Math.max(1, parts.length - 2); i < parts.length; i++) {
            redacted.append(",").append(parts[i].trim());
        }
        return redacted.toString();
    }
}
