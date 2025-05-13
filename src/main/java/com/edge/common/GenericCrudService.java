package com.edge.common;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.edge.exception.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public abstract class GenericCrudService<T, CommandDTO, ResponseDTO, ID> {
    protected final JpaRepository<T, ID> repository;
    protected final Function<T, ResponseDTO> mapperFunction;
    protected final Function<CommandDTO, T> toEntityFunction;
    protected final BiConsumer<T, CommandDTO> updateEntityFunction;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

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

    @Transactional
    public ResponseDTO create(CommandDTO dto) {
        beforeCreate(dto);
        T entity = toEntityFunction.apply(dto);
        repository.save(entity);
        afterCreate(entity);
        logger.info("Created entity: {}", entity);
        return mapperFunction.apply(entity);
    }

    @Transactional
    public Optional<ResponseDTO> update(ID id, CommandDTO dto) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));
        beforeUpdate(entity, dto);
        updateEntityFunction.accept(entity, dto);
        repository.save(entity);
        afterUpdate(entity);
        logger.info("Updated entity with id: {}", id);
        return Optional.ofNullable(mapperFunction.apply(entity));
    }

    @Transactional(readOnly = true)
    public Optional<ResponseDTO> get(ID id) {
        return repository.findById(id)
                .map(mapperFunction::apply);
    }

    @Transactional(readOnly = true)
    public List<ResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(mapperFunction)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));
        beforeDelete(entity);
        repository.delete(entity);
        afterDelete(entity);
        logger.info("Deleted entity with id: {}", id);
    }

    // Batch create
    @Transactional
    public List<ResponseDTO> batchCreate(List<CommandDTO> objects) {
        // Call beforeCreate for each DTO
        objects.forEach(this::beforeCreate);
        // Map DTOs to entities
        List<T> entities = objects.stream().map(toEntityFunction).collect(Collectors.toList());
        // Use repository batch save
        List<T> savedEntities = repository.saveAll(entities);
        // Call afterCreate for each saved entity
        savedEntities.forEach(this::afterCreate);
        logger.info("Batch created {} entities", savedEntities.size());
        // Map to response DTOs
        return savedEntities.stream().map(mapperFunction).collect(Collectors.toList());
    }

    // Batch delete
    @Transactional
    public void batchDelete(List<ID> ids) {
        ids.forEach(this::delete);
    }

    // Custom query using Specification (if repository supports it)
    @Transactional(readOnly = true)
    public List<ResponseDTO> findAllBySpecification(Specification<T> spec) {
        JpaSpecificationExecutor<T> specRepo = getSpecificationExecutor();
        if (specRepo == null) {
            throw new UnsupportedOperationException("Repository does not support Specification queries");
        }
        return specRepo.findAll(spec).stream().map(mapperFunction).collect(Collectors.toList());
    }

    /**
     * Override this in subclasses if Specification support is needed.
     */
    protected JpaSpecificationExecutor<T> getSpecificationExecutor() {
        return null;
    }

    // Hooks for audit/pre/post-processing
    protected void beforeCreate(CommandDTO dto) {
    }

    protected void afterCreate(T entity) {
    }

    protected void beforeUpdate(T entity, CommandDTO dto) {
    }

    protected void afterUpdate(T entity) {
    }

    protected void beforeDelete(T entity) {
    }

    protected void afterDelete(T entity) {
    }
}