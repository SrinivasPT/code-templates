// Test suite for ProductDomainService
package com.edge.product.domain.service;

import com.edge.product.domain.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductDomainServiceTest {
    private final ProductDomainService domainService = new ProductDomainService();

    @Test
    void testValidateProductDomainRules_validProduct() {
        Product product = Product.builder()
                .name("Valid Product")
                .price(BigDecimal.valueOf(10))
                .build();
        assertTrue(domainService.validateProductDomainRules(product));
    }

    @Test
    void testValidateProductDomainRules_invalidProduct() {
        Product product = Product.builder()
                .name("Invalid Product")
                .price(BigDecimal.ZERO)
                .build();
        assertFalse(domainService.validateProductDomainRules(product));
    }
}
