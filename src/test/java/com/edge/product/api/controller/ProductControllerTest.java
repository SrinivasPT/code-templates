// Test suite for ProductController (uses MockMvc)
package com.edge.product.api.controller;

import com.edge.product.api.dto.ProductDTO;
import com.edge.product.application.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("Should return a product when requested by ID")
    @WithMockUser
    void testGetProductById() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        ProductDTO dto = ProductDTO.builder()
                .id(id)
                .name("Test")
                .price(BigDecimal.TEN)
                .build();
        when(productService.get(id)).thenReturn(java.util.Optional.of(dto));

        // Act & Assert
        mockMvc.perform(get("/api/products/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.price").value("10"));
    }

    @Test
    @DisplayName("Should return 404 when product is not found")
    @WithMockUser
    void testGetProductByIdNotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(productService.get(id)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/products/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create a new product")
    @WithMockUser
    void testCreateProduct() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        ProductDTO requestDto = ProductDTO.builder()
                .name("New Product")
                .price(BigDecimal.valueOf(29.99))
                .build();

        ProductDTO responseDto = ProductDTO.builder()
                .id(id)
                .name("New Product")
                .price(BigDecimal.valueOf(29.99))
                .build();

        when(productService.create(any(ProductDTO.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.price").value("29.99"));
    }

    @Test
    @DisplayName("Should return all products")
    @WithMockUser
    void testGetAllProducts() throws Exception {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        List<ProductDTO> products = Arrays.asList(
                ProductDTO.builder()
                        .id(id1)
                        .name("Product 1")
                        .price(BigDecimal.TEN)
                        .build(),
                ProductDTO.builder()
                        .id(id2)
                        .name("Product 2")
                        .price(BigDecimal.valueOf(20))
                        .build());

        when(productService.findAll()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].id").value(id2.toString()))
                .andExpect(jsonPath("$[1].name").value("Product 2"));
    }
}
