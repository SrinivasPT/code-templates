package com.edge.product.domain.validation;

import java.math.BigDecimal;

// This file has been deprecated. Use com.edge.common.ValidationGroup instead.
public interface ProductValidations {
    interface Create {
    }

    interface Update {
    }

    String getName();

    BigDecimal getPrice();
}