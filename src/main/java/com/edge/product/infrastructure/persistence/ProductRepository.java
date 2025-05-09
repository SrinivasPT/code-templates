package com.edge.product.infrastructure.persistence;

import com.edge.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Product repository interface in the infrastructure layer.
 * Extends Spring Data JpaRepository.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    // Add any custom query methods you need
}