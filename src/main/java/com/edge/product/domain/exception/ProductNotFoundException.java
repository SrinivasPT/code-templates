package com.edge.product.domain.exception;

import com.edge.exception.EntityNotFoundException;

import java.util.UUID;

/**
 * Domain-specific exception for when a product cannot be found.
 */
public class ProductNotFoundException extends EntityNotFoundException {
    
    public ProductNotFoundException(String message) {
        super(message);
    }
    
    public ProductNotFoundException(UUID id) {
        super("Product not found with ID: " + id);
    }
}