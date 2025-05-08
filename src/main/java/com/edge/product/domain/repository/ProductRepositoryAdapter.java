package com.edge.product.domain.repository;

import com.edge.common.GenericRepository;
import com.edge.product.domain.entity.Product;
import org.springframework.stereotype.Component;

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
    public Product findById(UUID id) {
        return repository.findById(id).orElse(null);
    }
}