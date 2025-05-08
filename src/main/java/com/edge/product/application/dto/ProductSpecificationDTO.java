package com.edge.product.application.dto;

import com.edge.product.domain.validation.ProductSpecificationValidations;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpecificationDTO implements ProductSpecificationValidations {
    private UUID id;
    private String name;
    private String value;
}