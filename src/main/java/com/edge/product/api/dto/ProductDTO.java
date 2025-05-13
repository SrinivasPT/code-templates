package com.edge.product.api.dto;

import com.edge.common.ValidationGroup;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ProductDTO {
    private UUID id;

    @NotBlank(message = "Name is mandatory", groups = ValidationGroup.Create.class)
    @Size(max = 100, message = "Name must be less than 100 characters", groups = { ValidationGroup.Create.class,
            ValidationGroup.Update.class })
    private String name;

    private String description;

    @NotNull(message = "Price is mandatory", groups = ValidationGroup.Create.class)
    @DecimalMax(value = "100000", message = "Price must not exceed 100000", groups = { ValidationGroup.Create.class,
            ValidationGroup.Update.class })
    private BigDecimal price;

    private Long version;
    private List<ProductSpecificationDTO> specifications = new ArrayList<>();
}