package com.edge.product.domain.validation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public interface ProductSpecificationValidations {
    interface Create {}
    interface Update {}

    @NotBlank(message = "Specification name is mandatory", groups = Create.class)
    @Size(max = 100, message = "Specification name must be less than 100 characters", groups = {Create.class, Update.class})
    String getName();

    @NotBlank(message = "Specification value is mandatory", groups = Create.class)
    String getValue();
}