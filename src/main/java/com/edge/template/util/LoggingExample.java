package com.edge.template.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Example class demonstrating various logging features with SLF4J and Logback.
 */
@Component
public class LoggingExample {

    private static final Logger logger = LoggerFactory.getLogger(LoggingExample.class);

    /**
     * Demonstrates different logging levels
     */
    public void demonstrateLoggingLevels() {
        logger.trace("This is a TRACE level message");
        logger.debug("This is a DEBUG level message");
        logger.info("This is an INFO level message");
        logger.warn("This is a WARN level message");
        logger.error("This is an ERROR level message");
    }

    /**
     * Demonstrates logging with parameters
     */
    public void demonstrateParameterizedLogging(String userId, String action) {
        // Preferred way - using parameterized messages
        logger.info("User {} performed action: {}", userId, action);
        
        // Avoid string concatenation in logs as it creates objects even if log level is not enabled
        // BAD: logger.debug("User " + userId + " performed action: " + action);
    }

    /**
     * Demonstrates exception logging
     */
    public void demonstrateExceptionLogging() {
        try {
            throw new RuntimeException("Simulated exception");
        } catch (Exception e) {
            // Log exception with stack trace
            logger.error("An error occurred during processing", e);
        }
    }

    /**
     * Demonstrates using MDC (Mapped Diagnostic Context) for adding context to logs
     */
    public void demonstrateMDC(String requestId, String userId) {
        try {
            // Add context information to all log messages in this thread
            MDC.put("requestId", requestId);
            MDC.put("userId", userId);
            
            logger.info("Processing request");
            
            // Perform some operations
            logger.debug("Request details processed");
            
            logger.info("Request completed successfully");
        } finally {
            // Always clear the MDC when done
            MDC.clear();
        }
    }
}