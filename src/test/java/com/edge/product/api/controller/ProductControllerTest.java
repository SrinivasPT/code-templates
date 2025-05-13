// Test suite for ProductController (basic structure, uses MockMvc)
package com.edge.product.api.controller;

import com.edge.product.api.dto.ProductDTO;
import com.edge.product.application.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService productService;

    @Test
    @WithMockUser
    void testGetProductById() throws Exception {
        UUID id = UUID.randomUUID();
        ProductDTO dto = ProductDTO.builder().id(id).name("Test").price(BigDecimal.TEN).build();
        when(productService.get(id)).thenReturn(java.util.Optional.of(dto));

        System.out.println("Testing endpoint: /api/products/" + id);

        mockMvc.perform(get("/api/products/" + id))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Test"));
    }
}
