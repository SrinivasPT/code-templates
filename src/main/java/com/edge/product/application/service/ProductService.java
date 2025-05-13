package com.edge.product.application.service;

import com.edge.product.api.dto.ProductDTO;
import com.edge.product.domain.entity.Product;
import com.edge.common.GenericCrudService;
import com.edge.product.infrastructure.mapper.ProductMapper;
import com.edge.product.infrastructure.persistence.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Service
@Validated
public class ProductService extends GenericCrudService<Product, ProductDTO, ProductDTO, UUID> {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository repository, ProductMapper productMapper) {
        super(
                repository,
                productMapper::toDTO,
                dto -> productMapper.toEntity(dto),
                (entity, dto) -> productMapper.updateEntityFromDto(dto, entity));
    }

    @Override
    protected void beforeCreate(ProductDTO dto) {
        logger.info("beforeCreate called for ProductDTO: {}", dto);
    }

    @Override
    protected void afterCreate(Product entity) {
        logger.info("afterCreate called for Product: {}", entity);
    }

    @Override
    protected void beforeUpdate(Product entity, ProductDTO dto) {
        logger.info("beforeUpdate called for Product: {}, ProductDTO: {}", entity, dto);
    }

    @Override
    protected void afterUpdate(Product entity) {
        logger.info("afterUpdate called for Product: {}", entity);
    }

    @Override
    protected void beforeDelete(Product entity) {
        logger.info("beforeDelete called for Product: {}", entity);
    }

    @Override
    protected void afterDelete(Product entity) {
        logger.info("afterDelete called for Product: {}", entity);
    }
}