# GitHub Copilot Instructions for Generic User Management System

## Overview
This project implements a **Generic User Management System** using **CQRS**, **DDD**, **REST**, and **SOLID** principles in **Java** with **Spring Boot**, **Spring Data JPA**, **MapStruct**, **Lombok**, and **Jakarta Validation**. The goal is to generate modular, maintainable, and minimal code for new entities, services, controllers, or custom operations while adhering to the established patterns and folder structure.

## Folder Structure
The codebase follows a **DDD**-based folder structure with bounded contexts. Place generated files in the appropriate directories:

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── edge/
│   │           ├── common/
│   │           │   ├── config/               # Shared configuration (e.g., MapStruct, JPA)
│   │           │   ├── exception/            # Custom exceptions (e.g., EntityNotFoundException)
│   │           │   ├── generic/              # Generic components (GenericCrudService, GenericCrudController)
│   │           │   └── web/                  # Global web config (e.g., GlobalExceptionHandler)
│   │           ├── <entity>/                 # Bounded context (e.g., user, product)
│   │           │   ├── domain/               # Domain layer
│   │           │   │   ├── entity/           # Entities (e.g., User.java)
│   │           │   │   ├── repository/       # Repositories (e.g., UserRepository.java)
│   │           │   │   └── validation/       # Validation interfaces (e.g., UserValidations.java)
│   │           │   ├── application/          # Application layer
│   │           │   │   ├── dto/              # DTOs (e.g., UserCommandDTO.java, UserResponseDTO.java)
│   │           │   │   ├── mapper/           # Mappers (e.g., UserMapper.java)
│   │           │   │   └── service/          # Services (e.g., UserService.java)
│   │           │   └── presentation/         # Presentation layer
│   │           │       └── controller/       # Controllers (e.g., UserController.java)
│   └── resources/
│       ├── application.properties            # Spring Boot configuration
├── test/
│   ├── java/
│   │   └── com/
│   │       └── edge/
│   │           ├── <entity>/                 # Tests for bounded context
│   │           │   ├── application/          # Service tests (e.g., UserServiceTest.java)
│   │           │   └── presentation/         # Controller tests (e.g., UserControllerTest.java)
└── copilot-instructions.md                   # This file
```

- **Package**: Use `com.edge.<entity>` for each bounded context (e.g., `com.edge.product`).
- **File Placement**:
  - Entity: `src/main/java/com/edge/<entity>/domain/entity/<Entity>.java`
  - Validation: `src/main/java/com/edge/<entity>/domain/validation/<Entity>Validations.java`
  - DTOs: `src/main/java/com/edge/<entity>/application/dto/<Entity>CommandDTO.java`, `<Entity>ResponseDTO.java`
  - Mapper: `src/main/java/com/edge/<entity>/application/mapper/<Entity>Mapper.java`
  - Repository: `src/main/java/com/edge/<entity>/domain/repository/<Entity>Repository.java`
  - Service: `src/main/java/com/edge/<entity>/application/service/<Entity>Service.java`
  - Controller: `src/main/java/com/edge/<entity>/presentation/controller/<Entity>Controller.java`
  - Tests: `src/test/java/com/edge/<entity>/application/<Entity>ServiceTest.java`, `presentation/<Entity>ControllerTest.java`

## Architecture and Patterns
1. **CQRS**:
   - Separate **commands** (create/update) and **queries** (read) using `CommandDTO` and `ResponseDTO`.
   - Use `GenericCrudService` for CRUD operations, with entity-specific overrides.
2. **DDD**:
   - Organize by **bounded context** (e.g., `com.edge.user`).
   - Use **entities**, **repositories**, and **services** for domain logic.
3. **REST**:
   - Expose endpoints via `GenericCrudController` (e.g., `POST /<entities>`, `GET /<entities>/{id}`).
   - Support custom endpoints (e.g., `POST /<entities>/{id}/<action>`).
4. **SOLID**:
   - **S**: Single responsibility per class.
   - **O**: Extend via abstract methods/interfaces.
   - **L**: Use generic types for substitutability.
   - **I**: Focused interfaces (e.g., `GenericRepository`).
   - **D**: Inject dependencies via interfaces.
5. **Validation**:
   - Use `<Entity>Validations` interface with `Create` and `Update` groups.
   - Apply Jakarta Validation annotations (e.g., `@NotBlank`, `@Email`).
6. **Mapping**:
   - Use **MapStruct** for DTO-entity conversions.
   - Generate UUIDs in mappers for new entities.
7. **Persistence**:
   - Use **Spring Data JPA** repositories extending `JpaRepository` and `GenericRepository`.
8. **Error Handling**:
   - Throw `EntityNotFoundException` for missing entities.
   - Handle errors via `GlobalExceptionHandler` (`@RestControllerAdvice`).

## Template for New Entity
Use this template to generate code for a new entity (e.g., `Product`). Replace placeholders (`<Entity>`, `<entity>`, `<ID>`, `<Field>`, `<entities>`) with entity-specific details. Place files in the correct directories as specified.

```java
package com.edge.<entity>.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class <Entity> {
    @Id
    private <ID> id;
    private String <field>;
}
```

```java
package com.edge.<entity>.domain.validation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

interface <Entity>Validations {
    @NotBlank(message = "<Field> is mandatory", groups = Create.class)
    @Size(max = 100, message = "<Field> must be less than 100 characters", groups = {Create.class, Update.class})
    String get<Field>();
}
```

```java
package com.edge.<entity>.application.dto;

import com.edge.<entity>.domain.validation.<Entity>Validations;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class <Entity>CommandDTO implements <Entity>Validations {
    private String <field>;
}
```

```java
package com.edge.<entity>.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class <Entity>ResponseDTO {
    private <ID> id;
    private String <field>;
}
```

```java
package com.edge.<entity>.application.mapper;

import com.edge.<entity>.application.dto.<Entity>CommandDTO;
import com.edge.<entity>.application.dto.<Entity>ResponseDTO;
import com.edge.<entity>.domain.entity.<Entity>;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface <Entity>Mapper {
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    <Entity> toEntity(<Entity>CommandDTO dto);
    <Entity>ResponseDTO toResponseDTO(<Entity> entity);
}
```

```java
package com.edge.<entity>.domain.repository;

import com.edge.<entity>.domain.entity.<Entity>;
import com.edge.common.generic.GenericRepository;
import org.springframework.data.jpa.repository.JpaRepository;

interface <Entity>Repository extends JpaRepository<<Entity>, <ID>>, GenericRepository<<Entity>, <ID>> {}
```

```java
package com.edge.<entity>.application.service;

import com.edge.<entity>.application.dto.<Entity>CommandDTO;
import com.edge.<entity>.application.dto.<Entity>ResponseDTO;
import com.edge.<entity>.application.mapper.<Entity>Mapper;
import com.edge.<entity>.domain.entity.<Entity>;
import com.edge.<entity>.domain.repository.<Entity>Repository;
import com.edge.common.generic.GenericCrudService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
class <Entity>Service extends GenericCrudService<<Entity>, <Entity>CommandDTO, <Entity>ResponseDTO, <ID>> {
    private final <Entity>Mapper <entity>Mapper;

    <Entity>Service(<Entity>Repository repository, <Entity>Mapper <entity>Mapper) {
        super(repository, <entity>Mapper);
        this.<entity>Mapper = <entity>Mapper;
    }

    @Override
    protected <Entity> toEntity(@Validated(Create.class) <Entity>CommandDTO dto) {
        return <entity>Mapper.toEntity(dto);
    }

    @Override
    protected <Entity>ResponseDTO toResponseDTO(<Entity> entity) {
        return <entity>Mapper.toResponseDTO(entity);
    }

    @Override
    protected void updateEntity(<Entity> entity, @Validated(Update.class) <Entity>CommandDTO dto) {
        entity.set<Field>(dto.get<Field>());
    }
}
```

```java
package com.edge.<entity>.presentation.controller;

import com.edge.<entity>.application.dto.<Entity>CommandDTO;
import com.edge.<entity>.application.dto.<Entity>ResponseDTO;
import com.edge.<entity>.application.service.<Entity>Service;
import com.edge.<entity>.domain.entity.<Entity>;
import com.edge.common.generic.GenericCrudController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/<entities>")
class <Entity>Controller extends GenericCrudController<<Entity>, <Entity>CommandDTO, <Entity>ResponseDTO, <ID>> {
    <Entity>Controller(<Entity>Service service) {
        super(service);
    }
}
```

## Instructions for Copilot
1. **Folder Structure**:
   - Place generated files in the correct directories (e.g., `src/main/java/com/edge/<entity>/domain/entity/<Entity>.java`).
   - Use the package `com.edge.<entity>` for each bounded context.
   - Ensure test files go in `src/test/java/com/edge/<entity>`.

2. **Contextual Comments**:
   - Add comments to guide Copilot. Example:
     ```java
     // Generate a Product entity in src/main/java/com/edge/product/domain/entity/Product.java
     // Fields: id (UUID), name (String)
     // Validation: name is required for create, max 100 chars
     // Include CommandDTO, ResponseDTO, Mapper, Repository, Service, Controller
     // Follow the Generic User Management System pattern: CQRS, DDD, REST, SOLID
     ```
   - Place comments in the target file or a temporary file in the correct directory.

3. **Prompt Files**:
   - Store this file (`copilot-instructions.md`) in the project root.
   - Reference it in comments or keep it open in the IDE for Copilot to use as context.

4. **Code Completion**:
   - Create the target file in the correct directory (e.g., `src/main/java/com/edge/product/domain/entity/Product.java`).
   - Start typing the package and class declaration, and Copilot will suggest code based on the template.
   - Use the template as a starting point for manual edits or let Copilot fill in details.

5. **Custom Operations**:
   - For non-CRUD operations, add comments in the service class:
     ```java
     // Add a method to <Entity>Service to mark the entity as discontinued
     // Update the discontinued field and save
     // Return the updated ResponseDTO
     ```

6. **Validation and Testing**:
   - Ensure Copilot includes validation groups (`Create`, `Update`) and `@Validated` annotations.
   - Request tests with comments:
     ```java
     // Generate a unit test for <Entity>Service.create in src/test/java/com/edge/<entity>/application/<Entity>ServiceTest.java
     // Use JUnit and Mockito
     ```

## Conventions
- **Naming**:
  - Entity: `<Entity>` (e.g., `Product`).
  - DTOs: `<Entity>CommandDTO`, `<Entity>ResponseDTO`.
  - Mapper: `<Entity>Mapper`.
  - Repository: `<Entity>Repository`.
  - Service: `<Entity>Service`.
  - Controller: `<Entity>Controller`.
- **Packages**: `com.edge.<entity>.<layer>.<sublayer>` (e.g., `com.edge.product.domain.entity`).
- **File Names**: Match class names (e.g., `Product.java`, `ProductCommandDTO.java`).
- **Validation**: Use `<Entity>Validations` with `Create` and `Update` groups.
- **Error Handling**: Throw `EntityNotFoundException` for missing entities.
- **Testing**: Place tests in `src/test/java/com/edge/<entity>`.

## Example Prompt
To generate a `Product` entity:
```java
// Generate a Product entity in src/main/java/com/edge/product/domain/entity/Product.java
// Fields: id (UUID), name (String)
// Validation: name is required for create, max 100 chars
// Include CommandDTO, ResponseDTO, Mapper, Repository, Service, Controller
// Place files in:
// - Entity: src/main/java/com/edge/product/domain/entity
// - Validation: src/main/java/com/edge/product/domain/validation
// - DTOs: src/main/java/com/edge/product/application/dto
// - Mapper: src/main/java/com/edge/product/application/mapper
// - Repository: src/main/java/com/edge/product/domain/repository
// - Service: src/main/java/com/edge/product/application/service
// - Controller: src/main/java/com/edge/product/presentation/controller
// Follow the Generic User Management System pattern: CQRS, DDD, REST, SOLID
```

Copilot will generate files in the specified directories with correct packages.

## Notes
- **Review Generated Code**: Check for correct package declarations, file locations, and adherence to patterns.
- **Dependencies**: Ensure `pom.xml` includes Spring Boot, JPA, Lombok, MapStruct, Jakarta Validation, JUnit, and Mockito.
- **Testing**: Request tests explicitly to ensure coverage.
- **Refactoring**: Periodically review generated code to avoid duplication or complexity.