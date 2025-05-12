package com.edge.product.api.dto;

import com.edge.product.domain.validation.ProductValidations;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO implements ProductValidations {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long version;
    private List<ProductSpecificationDTO> specifications = new ArrayList<>();
}