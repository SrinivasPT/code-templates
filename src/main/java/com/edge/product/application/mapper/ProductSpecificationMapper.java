package com.edge.product.application.mapper;

import com.edge.product.application.dto.ProductSpecificationDTO;
import com.edge.product.domain.entity.ProductSpecification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface ProductSpecificationMapper {
    
    @Mapping(target = "id", expression = "java(dto.getId() == null ? UUID.randomUUID() : dto.getId())")
    ProductSpecification toEntity(ProductSpecificationDTO dto);
    
    ProductSpecificationDTO toDTO(ProductSpecification entity);
}