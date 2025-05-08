package com.edge.product.domain.validation;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public interface ProductValidations {
    interface Create {}
    interface Update {}

    @NotBlank(message = "Name is mandatory", groups = Create.class)
    @Size(max = 100, message = "Name must be less than 100 characters", groups = {Create.class, Update.class})
    String getName();

    @NotNull(message = "Price is mandatory", groups = Create.class)
    @DecimalMax(value = "100000", message = "Price must not exceed 100000", groups = {Create.class, Update.class})
    BigDecimal getPrice();
}