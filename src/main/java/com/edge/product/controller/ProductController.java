package com.edge.product.controller;

import com.edge.product.application.dto.ProductDTO;
import com.edge.product.application.service.ProductService;
import com.edge.product.domain.entity.Product;
import com.edge.common.GenericCrudController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController extends GenericCrudController<Product, ProductDTO, ProductDTO, UUID> {
    
    public ProductController(ProductService service) {
        super(service);
    }
}