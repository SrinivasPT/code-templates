package com.edge.product.infrastructure.persistence;

import com.edge.product.domain.repository.ProductRepository;
import com.edge.product.domain.repository.ProductRepositoryAdapter;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the ProductRepositoryAdapter interface.
 * This class sits in the infrastructure layer and provides the concrete implementation
 * for product persistence operations.
 */
@Repository
public class ProductRepositoryImpl extends ProductRepositoryAdapter {
    
    public ProductRepositoryImpl(ProductRepository repository) {
        super(repository);
    }
}