package com.edge.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public abstract class GenericCrudController<T, CommandDTO, ResponseDTO, ID> {
    protected final GenericCrudService<T, CommandDTO, ResponseDTO, ID> service;

    protected GenericCrudController(GenericCrudService<T, CommandDTO, ResponseDTO, ID> service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> create(@Valid @RequestBody CommandDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> update(@PathVariable ID id, @Valid @RequestBody CommandDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> get(@PathVariable ID id) {
        return ResponseEntity.ok(service.get(id));
    }
}