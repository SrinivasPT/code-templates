package com.edge.product.application.mapper;

import com.edge.product.application.dto.ProductDTO;
import com.edge.product.domain.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class, uses = ProductSpecificationMapper.class)
public interface ProductMapper {
    
    @Mapping(target = "id", expression = "java(dto.getId() == null ? UUID.randomUUID() : dto.getId())")
    Product toEntity(ProductDTO dto);
    
    ProductDTO toDTO(Product entity);
    
    // New method to update an existing entity
    void updateEntityFromDto(ProductDTO dto, @MappingTarget Product entity);
}