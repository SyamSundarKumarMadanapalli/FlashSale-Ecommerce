package com.syamsundar.product_service.product.service;

import com.syamsundar.product_service.common.exception.ProductNotFoundException;
import com.syamsundar.product_service.product.dto.ProductResponse;
import com.syamsundar.product_service.product.entity.Product;
import com.syamsundar.product_service.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductCacheService {
    private final ProductRepository productRepository;

    @Cacheable(value = "products", key = "#productId")
    public ProductResponse getProduct(UUID productId){
        System.out.println("Get product from DataBase+++++++++++++++++++++++++++++++++++++++++++" + productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }
}
