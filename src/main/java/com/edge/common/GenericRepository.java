package com.edge.common;

import java.util.Optional;
import org.springframework.lang.NonNull;

public interface GenericRepository<T, ID> {
    void save(T entity);
    @NonNull
    Optional<T> findById(ID id);
}