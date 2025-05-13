package com.edge.product.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import com.edge.common.ValidationGroup;

@Entity
@Table(name = "product_specifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductSpecification {
    @Id
    private UUID id;

    @NotBlank(message = "Specification name is mandatory", groups = ValidationGroup.Create.class)
    @Size(max = 100, message = "Specification name must be less than 100 characters", groups = {
            ValidationGroup.Create.class,
            ValidationGroup.Update.class })
    private String name;

    @NotBlank(message = "Specification value is mandatory", groups = ValidationGroup.Create.class)
    private String value;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}