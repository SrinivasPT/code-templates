package com.edge.product.application.service;

import com.edge.product.api.dto.ProductDTO;
import com.edge.product.domain.entity.Product;
import com.edge.common.GenericCrudService;
import com.edge.product.infrastructure.mapper.ProductMapper;
import com.edge.product.infrastructure.persistence.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@Validated
public class ProductService extends GenericCrudService<Product, ProductDTO, ProductDTO, UUID> {
    public ProductService(ProductRepository repository, ProductMapper productMapper) {
        super(
                repository,
                productMapper::toDTO,
                dto -> productMapper.toEntity(dto),
                (entity, dto) -> productMapper.updateEntityFromDto(dto, entity));
    }
}