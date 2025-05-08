package com.edge.product.domain.service;

import com.edge.product.domain.entity.Product;
import org.springframework.stereotype.Service;

/**
 * Domain service for Product-specific business logic that doesn't fit naturally
 * within the Product entity itself.
 */
@Service
public class ProductDomainService {
    
    /**
     * Example domain service method for product-specific business logic
     * 
     * @param product The product to validate
     * @return true if the product is valid according to domain rules
     */
    public boolean validateProductDomainRules(Product product) {
        // Domain-specific validation that goes beyond simple field validation
        // For example, complex business rules involving multiple fields or entities
        return product != null && product.getPrice() != null && product.getPrice().compareTo(java.math.BigDecimal.ZERO) > 0;
    }
}