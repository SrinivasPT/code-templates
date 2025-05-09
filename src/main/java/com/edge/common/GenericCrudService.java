package com.edge.common;

import java.util.function.Function;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.edge.exception.EntityNotFoundException;

@Service
public abstract class GenericCrudService<T, CommandDTO, ResponseDTO, ID> {
    protected final JpaRepository<T, ID> repository;
    protected final Function<T, ResponseDTO> mapperFunction;    protected GenericCrudService(JpaRepository<T, ID> repository, Function<T, ResponseDTO> mapperFunction) {
        this.repository = repository;
        this.mapperFunction = mapperFunction;
    }

    public ResponseDTO create(CommandDTO dto) {
        T entity = toEntity(dto);
        repository.save(entity);
        return toResponseDTO(entity);
    }    public ResponseDTO update(ID id, CommandDTO dto) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        updateEntity(entity, dto);
        repository.save(entity);
        return toResponseDTO(entity);
    }

    public ResponseDTO get(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        return toResponseDTO(entity);
    }

    protected abstract T toEntity(CommandDTO dto);
    protected abstract ResponseDTO toResponseDTO(T entity);
    protected abstract void updateEntity(T entity, CommandDTO dto);
}