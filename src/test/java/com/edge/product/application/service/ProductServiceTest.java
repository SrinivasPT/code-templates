// Test suite for ProductService
package com.edge.product.application.service;

import com.edge.product.api.dto.ProductDTO;
import com.edge.product.domain.entity.Product;
import com.edge.product.infrastructure.mapper.ProductMapper;
import com.edge.product.infrastructure.persistence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct() {
        ProductDTO dto = ProductDTO.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .build();
        Product entity = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .build();
        when(productMapper.toEntity(dto)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(entity);
        when(productMapper.toDTO(entity)).thenReturn(dto);
        ProductDTO result = productService.create(dto);
        assertEquals(dto.getName(), result.getName());
        verify(productRepository, times(1)).save(entity);
    }
}
