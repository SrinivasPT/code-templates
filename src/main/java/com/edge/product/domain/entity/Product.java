package com.edge.product.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
        @Id
        private UUID id;

        @NotBlank(message = "Name is mandatory", groups = com.edge.product.domain.validation.ProductValidations.Create.class)
        @Size(max = 100, message = "Name must be less than 100 characters", groups = {
                        com.edge.product.domain.validation.ProductValidations.Create.class,
                        com.edge.product.domain.validation.ProductValidations.Update.class })
        private String name;

        private String description;

        @NotNull(message = "Price is mandatory", groups = com.edge.product.domain.validation.ProductValidations.Create.class)
        @DecimalMax(value = "100000", message = "Price must not exceed 100000", groups = {
                        com.edge.product.domain.validation.ProductValidations.Create.class,
                        com.edge.product.domain.validation.ProductValidations.Update.class })
        private BigDecimal price;

        @Version
        private Long version;

        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
        private final List<ProductSpecification> specifications = new ArrayList<>();
}
