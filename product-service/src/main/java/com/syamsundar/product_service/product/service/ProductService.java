package com.syamsundar.product_service.product.service;

import com.syamsundar.product_service.product.dto.CreateProductRequest;
import com.syamsundar.product_service.product.dto.ProductResponse;
import com.syamsundar.product_service.product.entity.Product;
import com.syamsundar.product_service.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(CreateProductRequest request){
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setTotalStock(request.getStock());
        product.setAvailableStock(request.getStock());

        Product savedProduct = productRepository.save(product);

        return ProductResponse.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .price(savedProduct.getPrice())
                .availableStock(savedProduct.getAvailableStock())
                .build();
    }
}
