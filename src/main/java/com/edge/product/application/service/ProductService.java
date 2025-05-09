package com.edge.product.application.service;

import com.edge.product.application.dto.ProductDTO;
import com.edge.product.application.mapper.ProductMapper;
import com.edge.product.domain.entity.Product;
import com.edge.product.domain.validation.ProductValidations;
import com.edge.common.GenericCrudService;
import com.edge.product.infrastructure.persistence.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@Validated
public class ProductService extends GenericCrudService<Product, ProductDTO, ProductDTO, UUID> {
    private final ProductMapper productMapper;    public ProductService(ProductRepository repository, ProductMapper productMapper) {
        super(repository, productMapper::toDTO);
        this.productMapper = productMapper;
    }

    @Override
    protected Product toEntity(@Validated(ProductValidations.Create.class) ProductDTO dto) {
        return productMapper.toEntity(dto);
    }

    @Override
    protected ProductDTO toResponseDTO(Product entity) {
        return productMapper.toDTO(entity);
    }

    @Override
    @Transactional
    protected void updateEntity(Product entity, @Validated(ProductValidations.Update.class) ProductDTO dto) {
        productMapper.updateEntityFromDto(dto, entity);
    }
}