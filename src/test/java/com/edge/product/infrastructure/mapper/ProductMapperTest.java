// Test suite for ProductMapper
package com.edge.product.infrastructure.mapper;

import com.edge.product.api.dto.ProductDTO;
import com.edge.product.domain.entity.Product;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {
    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void testToEntityAndToDTO() {
        ProductDTO dto = ProductDTO.builder()
                .name("MapStruct Product")
                .price(BigDecimal.valueOf(50))
                .build();
        Product entity = mapper.toEntity(dto);
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getPrice(), entity.getPrice());
        ProductDTO mappedDto = mapper.toDTO(entity);
        assertEquals(entity.getName(), mappedDto.getName());
        assertEquals(entity.getPrice(), mappedDto.getPrice());
    }
}
