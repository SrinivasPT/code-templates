package com.edge.product.infrastructure.mapper;

import com.edge.product.api.dto.ProductDTO;
import com.edge.product.api.dto.ProductSpecificationDTO;
import com.edge.product.domain.entity.Product;
import com.edge.product.domain.entity.ProductSpecification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;
import java.util.List;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface ProductMapper {
    
    @Mapping(target = "id", expression = "java(dto.getId() == null ? UUID.randomUUID() : dto.getId())")
    Product toEntity(ProductDTO dto);
    
    ProductDTO toDTO(Product entity);
    
    void updateEntityFromDto(ProductDTO dto, @MappingTarget Product entity);
    
    // ProductSpecification mapping methods
    @Mapping(target = "id", expression = "java(dto.getId() == null ? UUID.randomUUID() : dto.getId())")
    ProductSpecification toEntity(ProductSpecificationDTO dto);
    
    ProductSpecificationDTO toDTO(ProductSpecification entity);
    
    // List mapping methods
    List<ProductSpecification> toEntityList(List<ProductSpecificationDTO> dtoList);
    
    List<ProductSpecificationDTO> toDTOList(List<ProductSpecification> entityList);
}