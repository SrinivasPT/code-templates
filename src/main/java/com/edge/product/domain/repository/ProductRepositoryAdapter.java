package com.edge.product.domain.repository;

import com.edge.common.GenericRepository;
import com.edge.product.domain.entity.Product;
import com.edge.product.infrastructure.persistence.ProductRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProductRepositoryAdapter implements GenericRepository<Product, UUID> {
    private final ProductRepository repository;

    public ProductRepositoryAdapter(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Product entity) {
        repository.save(entity);
    }

    @Override
    @NonNull
    public Optional<Product> findById(UUID id) {
        return repository.findById(id);
    }
}
