package com.edge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration properties for log redaction.
 */
@Configuration
@ConfigurationProperties(prefix = "logging.redaction")
public class LogRedactionProperties {
    
    /**
     * Whether redaction is enabled
     */
    private boolean enabled = true;
    
    /**
     * List of field names to redact (default full redaction)
     */
    private List<String> fields = new ArrayList<>();
    
    /**
     * Map of field names to redaction types
     * Example: {"creditCard": "CREDIT_CARD", "email": "EMAIL"}
     */
    private Map<String, String> fieldTypes = new HashMap<>();
    
    /**
     * The string to use when replacing sensitive data
     */
    private String maskWith = "********";
    
    /**
     * Whether to enable JSON content inspection and redaction
     */
    private boolean jsonRedactionEnabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
    
    public Map<String, String> getFieldTypes() {
        return fieldTypes;
    }

    public void setFieldTypes(Map<String, String> fieldTypes) {
        this.fieldTypes = fieldTypes;
    }

    public String getMaskWith() {
        return maskWith;
    }

    public void setMaskWith(String maskWith) {
        this.maskWith = maskWith;
    }

    public boolean isJsonRedactionEnabled() {
        return jsonRedactionEnabled;
    }

    public void setJsonRedactionEnabled(boolean jsonRedactionEnabled) {
        this.jsonRedactionEnabled = jsonRedactionEnabled;
    }
}
