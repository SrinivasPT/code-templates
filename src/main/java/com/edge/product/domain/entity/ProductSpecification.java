package com.edge.product.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "product_specifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSpecification {
    @Id
    private UUID id;
    
    private String name;
    
    private String value;
}