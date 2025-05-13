package com.edge.common;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public abstract class GenericCrudService<T, CommandDTO, ResponseDTO, ID> {
    protected final JpaRepository<T, ID> repository;
    protected final Function<T, ResponseDTO> mapperFunction;
    protected final Function<CommandDTO, T> toEntityFunction;
    protected final BiConsumer<T, CommandDTO> updateEntityFunction;

    protected GenericCrudService(
            JpaRepository<T, ID> repository,
            Function<T, ResponseDTO> mapperFunction,
            Function<CommandDTO, T> toEntityFunction,
            BiConsumer<T, CommandDTO> updateEntityFunction) {
        this.repository = repository;
        this.mapperFunction = mapperFunction;
        this.toEntityFunction = toEntityFunction;
        this.updateEntityFunction = updateEntityFunction;
    }

    public ResponseDTO create(CommandDTO dto) {
        T entity = toEntityFunction.apply(dto);
        repository.save(entity);
        return mapperFunction.apply(entity);
    }

    public ResponseDTO update(ID id, CommandDTO dto) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entity not found"));
        updateEntityFunction.accept(entity, dto);
        repository.save(entity);
        return mapperFunction.apply(entity);
    }

    public ResponseDTO get(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entity not found"));
        return mapperFunction.apply(entity);
    }
}