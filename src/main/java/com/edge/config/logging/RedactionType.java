package com.edge.config.logging;

/**
 * Enum defining different types of redaction strategies.
 */
public enum RedactionType {
    /**
     * Full redaction - replaces the entire value with a mask
     */
    FULL,
    
    /**
     * Partial redaction for credit cards - shows only last 4 digits
     * Example: **** **** **** 1234
     */
    CREDIT_CARD,
    
    /**
     * Partial redaction for email addresses - shows first character and domain
     * Example: j*****@example.com
     */
    EMAIL,
    
    /**
     * Partial redaction for phone numbers - shows only last 4 digits
     * Example: ******1234
     */
    PHONE,
    
    /**
     * Partial redaction for SSN/government IDs - shows only last 4 digits
     * Example: ***-**-1234
     */
    SSN,
    
    /**
     * Partial redaction for addresses - shows only city and country
     * Example: *****, New York, USA
     */
    ADDRESS
}
