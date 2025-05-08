package com.edge.common;

public interface GenericRepository<T, ID> {
    void save(T entity);
    T findById(ID id);
}