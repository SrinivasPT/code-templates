package com.edge.config.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Test component that demonstrates the redaction functionality.
 * Only active when the "test-redaction" profile is enabled.
 */
@Component
@Profile("test-redaction")
public class RedactionTestComponent implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RedactionTestComponent.class);

    @Override
    public void run(String... args) {
        log.info("===== STARTING REDACTION TEST =====");
        
        // Test simple key-value full redaction
        log.info("User with password=Secret123! has logged in");
        log.info("API Token: token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");
        
        // Test partial redaction for different field types
        log.info("Credit card: creditCard=4111-1111-1111-1111 (should show last 4 digits)");
        log.info("Email address: email=john.doe@example.com (should preserve domain)");
        log.info("Phone number: phone=+1-555-123-4567 (should show last 4 digits)");
        log.info("Social security: ssn=123-45-6789 (should show last 4 digits)");
        log.info("Home address: address=123 Main St, Apt 4B, New York, NY 10001 (should show city/state)");
        
        // Test JSON object redaction with mixed redaction types
        log.info("User credentials: {\"username\":\"john.doe\",\"password\":\"secret123\",\"email\":\"john.doe@example.com\"}");
        
        // Test nested JSON redaction with various redaction types
        String complexJson = "{\n" +
                "  \"user\": {\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"credentials\": {\n" +
                "      \"username\": \"johndoe\",\n" +
                "      \"password\": \"p@ssw0rd\",\n" +
                "      \"email\": \"john.doe@example.com\"\n" +
                "    },\n" +
                "    \"payment\": {\n" +
                "      \"creditCard\": \"4111-1111-1111-1111\",\n" +
                "      \"expiry\": \"12/25\"\n" +
                "    },\n" +
                "    \"contact\": {\n" +
                "      \"phone\": \"+1-555-123-4567\",\n" +
                "      \"address\": \"123 Main St, Apt 4B, New York, NY 10001\"\n" +
                "    },\n" +
                "    \"governmentId\": {\n" +
                "      \"ssn\": \"123-45-6789\",\n" +
                "      \"issuedBy\": \"USA\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"transaction\": {\n" +
                "    \"amount\": 100.00,\n" +
                "    \"currency\": \"USD\",\n" +
                "    \"secretKey\": \"sk_test_abcdefghijklmnopqrstuvwxyz\"\n" +
                "  }\n" +
                "}";
        log.info("User transaction data: {}", complexJson);
        
        // Test JSON array redaction
        String jsonArray = "{\n" +
                "  \"users\": [\n" +
                "    {\"name\": \"John\", \"email\": \"john@example.com\", \"phone\": \"555-1234\"},\n" +
                "    {\"name\": \"Jane\", \"email\": \"jane@example.com\", \"phone\": \"555-5678\"}\n" +
                "  ]\n" +
                "}";
        log.info("User list: {}", jsonArray);
        
        log.info("===== REDACTION TEST COMPLETE =====");
    }
}
