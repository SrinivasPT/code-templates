package com.example.<entity>;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// --- Validation Interface ---
interface <Entity>Validations {
    @NotBlank(message = "<Field> is mandatory", groups = Create.class)
    @Size(max = 100, message = "<Field> must be less than 100 characters", groups = {Create.class, Update.class})
    String get<Field>();
}

// --- Entity ---
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class <Entity> {
    @Id
    private <ID> id;
    private String <field>;
}

// --- DTOs ---
@Data
@NoArgsConstructor
@AllArgsConstructor
class <Entity>CommandDTO implements <Entity>Validations {
    private String <field>;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class <Entity>ResponseDTO {
    private <ID> id;
    private String <field>;
}

// --- Mapper ---
@Mapper(componentModel = "spring")
interface <Entity>Mapper {
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    <Entity> toEntity(<Entity>CommandDTO dto);
    <Entity>ResponseDTO toResponseDTO(<Entity> entity);
}

// --- Repository ---
interface <Entity>Repository extends JpaRepository<<Entity>, <ID>>, GenericRepository<<Entity>, <ID>> {}

// --- Service ---
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

// --- Controller ---
@RestController
@RequestMapping("/<entities>")
class <Entity>Controller extends GenericCrudController<<Entity>, <Entity>CommandDTO, <Entity>ResponseDTO, <ID>> {
    <Entity>Controller(<Entity>Service service) {
        super(service);
    }
}