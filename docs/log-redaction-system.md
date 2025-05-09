# Log Redaction System

## Overview

The Log Redaction System is a centralized solution that automatically masks sensitive information in logs, providing comprehensive protection for confidential data. It works with both plain text log messages and structured JSON data, supporting both full and partial redaction of different field types.

## Features

- Redact sensitive information from log messages using pattern matching
- Support for different redaction strategies (full or partial) based on field type
- Intelligent field type inference based on field name
- Deep inspection and redaction of JSON objects in log messages
- Support for nested JSON objects and arrays
- Configurable through application properties
- Works with both console and file appenders

## Configuration

Configure the redaction system through the following properties in `application.properties`:

```properties
# Redaction Configuration
logging.redaction.enabled=true
logging.redaction.fields=password,secret,token,key,accessKey,secretKey,accountNumber,birthdate
logging.redaction.field-types=creditCard=CREDIT_CARD,email=EMAIL,phone=PHONE,ssn=SSN,socialSecurity=SSN,address=ADDRESS
logging.redaction.mask-with=********
logging.redaction.json-redaction-enabled=true
```

### Properties Explanation

- `logging.redaction.enabled`: Enable or disable the entire redaction system
- `logging.redaction.fields`: Comma-separated list of field names to redact (defaults to full redaction)
- `logging.redaction.field-types`: Field-specific redaction type mappings in format `fieldName=TYPE`
- `logging.redaction.mask-with`: The string to use when replacing sensitive data
- `logging.redaction.json-redaction-enabled`: Enable or disable JSON content inspection and redaction

## Redaction Types

The system supports various redaction strategies depending on the type of data:

| Redaction Type | Description | Example |
|---------------|-------------|---------|
| `FULL` | Complete redaction of the entire value | `password=********` |
| `CREDIT_CARD` | Shows only last 4 digits | `creditCard=**** **** **** 1234` |
| `EMAIL` | Shows first character and domain | `email=j********@example.com` |
| `PHONE` | Shows only last 4 digits | `phone=******4567` |
| `SSN` | Shows only last 4 digits | `ssn=***-**-1234` |
| `ADDRESS` | Shows only city and country | `address=*****, New York, USA` |

The system will:
1. Automatically infer the most appropriate redaction type based on the field name
2. Apply the explicit redaction type if one is specified in the configuration
3. Fall back to full redaction for unrecognized fields

## How It Works

The redaction system has several components:

1. **RedactingPatternLayout**: A custom Logback layout that processes log messages before they're written to appenders
2. **MaskingJsonConverter**: A utility class that handles JSON parsing and masking of sensitive fields
3. **RedactionConfig**: Configuration for field redaction based on field name
4. **RedactionType**: Enum defining different types of redaction strategies

The system works in two ways:

1. **Simple pattern matching**: Detects patterns like `password=123456` or `ssn: 123-45-6789` in log messages
2. **JSON processing**: Deep-inspects JSON structures, regardless of nesting level, to find and mask sensitive fields

## Examples

### Simple Text Redaction - Full Redaction

When someone logs:
```
User with password=Secret123! has logged in
```

The output becomes:
```
User with password=******** has logged in
```

### Simple Text Redaction - Partial Redaction

When someone logs:
```
Credit card: creditCard=4111-1111-1111-1111
Email address: email=john.doe@example.com
```

The output becomes:
```
Credit card: creditCard=**** **** **** 1111
Email address: email=j********@example.com
```

### JSON Redaction with Mixed Redaction Types

When someone logs:
```
User credentials: {"username":"john.doe","password":"secret123","email":"john@example.com","creditCard":"4111-1111-1111-1111"}
```

The output becomes:
```
User credentials: {"username":"john.doe","password":"********","email":"j********@example.com","creditCard":"**** **** **** 1111"}
```

## Adding and Configuring Fields to Redact

### Adding Fields with Default Type Inference

To add new fields with automatic type inference, update the `logging.redaction.fields` property:

```properties
logging.redaction.fields=password,secret,token,key,accessKey,secretKey,accountNumber
```

The system will automatically infer the most suitable redaction type based on the field name:

- Fields containing `password`, `secret`, `token`, `key`: `FULL` redaction
- Fields containing `credit`, `card`, `ccnum`: `CREDIT_CARD` redaction
- Fields containing `email`: `EMAIL` redaction
- Fields containing `phone`, `mobile`, `cell`: `PHONE` redaction
- Fields containing `ssn`, `social`, `security`: `SSN` redaction
- Fields containing `address`: `ADDRESS` redaction
- All other fields: `FULL` redaction (default)

### Explicitly Setting Field Types

For more precise control, configure the redaction type explicitly using the `logging.redaction.field-types` property:

```properties
logging.redaction.field-types=creditCard=CREDIT_CARD,email=EMAIL,phone=PHONE,ssn=SSN,address=ADDRESS,customerId=FULL
```

This is especially useful when:
- Field names don't match the standard naming patterns
- You want to override the automatic type inference
- You have custom fields with specific redaction requirements

## Testing

To test the redaction functionality, you can enable the `test-redaction` profile when starting the application:

```
java -jar your-app.jar --spring.profiles.active=test-redaction
```

This will run the `RedactionTestComponent` that demonstrates the redaction capabilities with various examples, including:
- Full redaction of passwords and secrets
- Partial redaction of credit cards, showing only last 4 digits
- Partial redaction of email addresses, showing first character and domain
- Partial redaction of phone numbers, showing only last 4 digits
- Partial redaction of SSNs, showing only last 4 digits
- Partial redaction of addresses, showing only city and country
