
# Spring Boot Entity Module Code Template

## Domain Layer

### Entity

```java
package com.edge.<entity>.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.<ID>Type;

@Entity
@Table(name = "<entities>")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class <Entity> {
    @Id
    private <ID> id;
    
    @NotBlank(message = "<Field> is mandatory", groups = com.edge.<entity>.domain.validation.<Entity>Validations.Create.class)
    @Size(max = 100, message = "<Field> must be less than 100 characters", groups = {
            com.edge.<entity>.domain.validation.<Entity>Validations.Create.class,
            com.edge.<entity>.domain.validation.<Entity>Validations.Update.class
    })
    private String <field>;
    // Add additional fields as needed
}
```

### Validation Interface

```java
package com.edge.<entity>.domain.validation;

/**
 * Validation groups for <Entity> validation
 */
public interface <Entity>Validations {
    /**
     * Validation group for create operations
     */
    interface Create {}
    
    /**
     * Validation group for update operations
     */
    interface Update {}
}
```

### Repository Adapter Interface

```java
package com.edge.<entity>.domain.repository;

import com.edge.common.GenericRepository;
import com.edge.<entity>.domain.entity.<Entity>;
import com.edge.<entity>.infrastructure.persistence.<Entity>Repository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.<ID>Type;

@Component
public class <Entity>RepositoryAdapter implements GenericRepository<<Entity>, <ID>> {
    private final <Entity>Repository repository;

    public <Entity>RepositoryAdapter(<Entity>Repository repository) {
        this.repository = repository;
    }

    @Override
    public void save(<Entity> entity) {
        repository.save(entity);
    }

    @Override
    @NonNull
    public Optional<<Entity>> findById(<ID> id) {
        return repository.findById(id);
    }
}
```

## API Layer

### DTOs

```java
package com.edge.<entity>.api.dto;

import com.edge.common.ValidationGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.<ID>Type;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class <Entity>DTO {
    private <ID> id;
    
    @NotBlank(message = "<Field> is mandatory", groups = ValidationGroup.Create.class)
    @Size(max = 100, message = "<Field> must be less than 100 characters", groups = {
            ValidationGroup.Create.class, ValidationGroup.Update.class
    })
    private String <field>;
    // Add additional fields as needed
}
```

## Infrastructure Layer

### Persistence Repository

```java
package com.edge.<entity>.infrastructure.persistence;

import com.edge.<entity>.domain.entity.<Entity>;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.<ID>Type;

/**
 * <Entity> repository interface in the infrastructure layer.
 * Extends Spring Data JpaRepository.
 */
@Repository
public interface <Entity>Repository extends JpaRepository<<Entity>, <ID>> {
    // Add any custom query methods you need
}
```

### Mapper

```java
package com.edge.<entity>.infrastructure.mapper;

import com.edge.<entity>.api.dto.<Entity>DTO;
import com.edge.<entity>.domain.entity.<Entity>;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.<ID>Type;

@Mapper(componentModel = "spring", imports = <ID>Type.class)
public interface <Entity>Mapper {
    @Mapping(target = "id", expression = "java(dto.getId() == null ? <ID>Type.randomUUID() : dto.getId())")
    <Entity> toEntity(<Entity>DTO dto);
    
    <Entity>DTO toDTO(<Entity> entity);
    
    // For updates
    void updateEntityFromDto(<Entity>DTO dto, @MappingTarget <Entity> entity);
}
```

## Application Layer

### Service

```java
package com.edge.<entity>.application.service;

import com.edge.<entity>.api.dto.<Entity>DTO;
import com.edge.<entity>.domain.entity.<Entity>;
import com.edge.common.GenericCrudService;
import com.edge.<entity>.infrastructure.mapper.<Entity>Mapper;
import com.edge.<entity>.infrastructure.persistence.<Entity>Repository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.<ID>Type;

@Service
@Validated
public class <Entity>Service extends GenericCrudService<<Entity>, <Entity>DTO, <Entity>DTO, <ID>> {
    private static final Logger logger = LoggerFactory.getLogger(<Entity>Service.class);

    public <Entity>Service(<Entity>Repository repository, <Entity>Mapper <entity>Mapper) {
        super(
                repository,
                <entity>Mapper::toDTO,
                dto -> <entity>Mapper.toEntity(dto),
                (entity, dto) -> <entity>Mapper.updateEntityFromDto(dto, entity));
    }

    @Override
    protected void beforeCreate(<Entity>DTO dto) {
        logger.info("beforeCreate called for <Entity>DTO: {}", dto);
        // Add pre-creation logic here
    }

    @Override
    protected void afterCreate(<Entity> entity) {
        logger.info("afterCreate called for <Entity>: {}", entity);
        // Add post-creation logic here
    }

    @Override
    protected void beforeUpdate(<Entity> entity, <Entity>DTO dto) {
        logger.info("beforeUpdate called for <Entity>: {}, <Entity>DTO: {}", entity, dto);
        // Add pre-update logic here
    }

    @Override
    protected void afterUpdate(<Entity> entity) {
        logger.info("afterUpdate called for <Entity>: {}", entity);
        // Add post-update logic here
    }

    @Override
    protected void beforeDelete(<Entity> entity) {
        logger.info("beforeDelete called for <Entity>: {}", entity);
        // Add pre-deletion logic here
    }

    @Override
    protected void afterDelete(<Entity> entity) {
        logger.info("afterDelete called for <Entity>: {}", entity);
        // Add post-deletion logic here
    }
}
```

### Controller

```java
package com.edge.<entity>.api.controller;

import com.edge.<entity>.api.dto.<Entity>DTO;
import com.edge.<entity>.application.service.<Entity>Service;
import com.edge.<entity>.domain.entity.<Entity>;
import com.edge.common.GenericCrudController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.<ID>Type;

@RestController
@RequestMapping("/api/<entities>")
public class <Entity>Controller extends GenericCrudController<<Entity>, <Entity>DTO, <Entity>DTO, <ID>> {
    
    public <Entity>Controller(<Entity>Service service) {
        super(service);
    }
    
    // Add custom endpoints here if needed
}
```

## Tests

### Service Test

```java
package com.edge.<entity>.application.service;

import com.edge.<entity>.api.dto.<Entity>DTO;
import com.edge.<entity>.application.service.<Entity>Service;
import com.edge.<entity>.domain.entity.<Entity>;
import com.edge.<entity>.infrastructure.mapper.<Entity>Mapper;
import com.edge.<entity>.infrastructure.persistence.<Entity>Repository;
import com.edge.exception.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.<ID>Type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class <Entity>ServiceTest {

    @Mock
    private <Entity>Repository repository;

    @Mock
    private <Entity>Mapper mapper;

    @InjectMocks
    private <Entity>Service service;

    private <Entity> entity;
    private <Entity>DTO dto;
    private <ID> id;

    @BeforeEach
    void setUp() {
        id = <ID>.randomUUID();
        entity = <Entity>.builder()
                .id(id)
                .<field>("<field>Value")
                .build();
        
        dto = <Entity>DTO.builder()
                .id(id)
                .<field>("<field>Value")
                .build();
        
        when(mapper.toEntity(any(<Entity>DTO.class))).thenReturn(entity);
        when(mapper.toDTO(any(<Entity>.class))).thenReturn(dto);
    }

    @Test
    void shouldCreateEntity() {
        when(repository.save(any(<Entity>.class))).thenReturn(entity);
        
        <Entity>DTO result = service.create(dto);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.get<Field>()).isEqualTo("<field>Value");
        verify(repository).save(entity);
    }

    @Test
    void shouldFindById() {
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        
        Optional<<Entity>DTO> result = service.findById(id);
        
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        verify(repository).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentEntity() {
        when(repository.findById(id)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> service.update(id, dto))
            .isInstanceOf(EntityNotFoundException.class);
        
        verify(repository).findById(id);
        verify(repository, never()).save(any());
    }
}
```

### Controller Test

```java
package com.edge.<entity>.api.controller;

import com.edge.<entity>.api.dto.<Entity>DTO;
import com.edge.<entity>.application.service.<Entity>Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;
import java.util.<ID>Type;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(<Entity>Controller.class)
public class <Entity>ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private <Entity>Service service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateEntity() throws Exception {
        <ID> id = <ID>.randomUUID();
        <Entity>DTO requestDTO = <Entity>DTO.builder()
                .<field>("<field>Value")
                .build();
        
        <Entity>DTO responseDTO = <Entity>DTO.builder()
                .id(id)
                .<field>("<field>Value")
                .build();
        
        when(service.create(any(<Entity>DTO.class))).thenReturn(responseDTO);
        
        mockMvc.perform(post("/api/<entities>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.<field>").value("<field>Value"));
    }

    @Test
    void shouldGetEntityById() throws Exception {
        <ID> id = <ID>.randomUUID();
        <Entity>DTO responseDTO = <Entity>DTO.builder()
                .id(id)
                .<field>("<field>Value")
                .build();
        
        when(service.get(id)).thenReturn(Optional.of(responseDTO));
        
        mockMvc.perform(get("/api/<entities>/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.<field>").value("<field>Value"));
    }

    @Test
    void shouldReturn404WhenEntityNotFound() throws Exception {
        <ID> id = <ID>.randomUUID();
        
        when(service.get(id)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/<entities>/" + id))
                .andExpect(status().isNotFound());
    }
}
```

## Instructions for Use

To use this template:

1. Replace the following placeholders with your specific entity information:
   - `<entity>`: Lowercase entity name (e.g., product)
   - `<Entity>`: Pascal case entity name (e.g., Product)
   - `<entities>`: Plural lowercase entity name (e.g., products)
   - `<field>`: Primary field name in camelCase (e.g., name)
   - `<Field>`: Primary field name in PascalCase (e.g., Name)
   - `<ID>`: ID type (usually UUID)
   - `<ID>Type`: Full type name (e.g., UUID or Long)

2. Follow the package structure:
   ```
   src/main/java/com/edge/<entity>/
      ├── api/
      │   ├── controller/
      │   └── dto/
      ├── application/
      │   └── service/
      ├── domain/
      │   ├── entity/
      │   ├── exception/
      │   ├── repository/
      │   ├── service/
      │   └── validation/
      └── infrastructure/
          ├── config/
          ├── external/
          ├── mapper/
          └── persistence/
   ```

3. Create corresponding test classes in the proper test package structure.

4. Add additional fields, methods, and validations as needed for your specific entity requirements.

5. Implement custom endpoints or business logic in service methods as needed.

## Example

For a "Product" entity with fields "name" and "price":

- Replace `<entity>` with `product`
- Replace `<Entity>` with `Product`
- Replace `<entities>` with `products`
- Replace `<field>` with `name` (primary field)
- Replace `<Field>` with `Name`
- Replace `<ID>` with `UUID`
- Replace `<ID>Type` with `UUID`

Then add additional fields like "price" to the entity, DTOs, and update methods.