package com.edge.common;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import jakarta.validation.groups.Default;

import java.util.List;
import java.util.Optional;

@RestController
public abstract class GenericCrudController<T, CommandDTO, ResponseDTO, ID> {
    protected final GenericCrudService<T, CommandDTO, ResponseDTO, ID> service;

    protected GenericCrudController(GenericCrudService<T, CommandDTO, ResponseDTO, ID> service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> create(
            @Validated({ Default.class, ValidationGroup.Create.class }) @RequestBody CommandDTO dto) {
        ResponseEntity<ResponseDTO> response = ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
        // Optionally log request receipt or response status here if needed
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> update(@PathVariable ID id,
            @Validated({ Default.class, ValidationGroup.Update.class }) @RequestBody CommandDTO dto) {
        Optional<ResponseDTO> updated = service.update(id, dto);
        // Optionally log request receipt or response status here if needed
        return updated.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> get(@PathVariable ID id) {
        Optional<ResponseDTO> result = service.get(id);
        return result.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        service.delete(id);
        // Optionally log request receipt or response status here if needed
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ResponseDTO>> batchCreate(@RequestBody List<CommandDTO> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.batchCreate(dtos));
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestParam List<ID> ids) {
        service.batchDelete(ids);
        return ResponseEntity.noContent().build();
    }
}