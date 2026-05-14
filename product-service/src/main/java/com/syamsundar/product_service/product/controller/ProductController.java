package com.syamsundar.product_service.product.controller;

import com.syamsundar.product_service.product.dto.CreateProductRequest;
import com.syamsundar.product_service.product.dto.ProductResponse;
import com.syamsundar.product_service.product.dto.PurchaseRequest;
import com.syamsundar.product_service.product.dto.PurchaseResponse;
import com.syamsundar.product_service.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request){
        return productService.createProduct(request);
    }

    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable UUID productId){
        return productService.getProduct(productId);
    }

    @PostMapping("/purchase")
    public PurchaseResponse purchaseProduct(@Valid @RequestBody PurchaseRequest request){
        return productService.purchaseProduct(request);
    }
}
