package com.syamsundar.product_service.product.service;

import com.syamsundar.product_service.common.exception.OutOfStockException;
import com.syamsundar.product_service.common.exception.ProductAlreadyExistsException;
import com.syamsundar.product_service.common.exception.ProductNotFoundException;
import com.syamsundar.product_service.product.dto.CreateProductRequest;
import com.syamsundar.product_service.product.dto.ProductResponse;
import com.syamsundar.product_service.product.dto.PurchaseRequest;
import com.syamsundar.product_service.product.dto.PurchaseResponse;
import com.syamsundar.product_service.product.entity.Product;
import com.syamsundar.product_service.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

        if(productRepository.existsByName(product.getName())){
            throw new ProductAlreadyExistsException("Product already exists");
        }
        Product savedProduct = productRepository.save(product);

        return ProductResponse.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .price(savedProduct.getPrice())
                .availableStock(savedProduct.getAvailableStock())
                .build();
    }

    public ProductResponse getProduct(UUID productId){

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .availableStock(product.getAvailableStock())
                .build();
    }

    @Transactional
    public PurchaseResponse purchaseProduct(PurchaseRequest request){

        int updatedRows = productRepository.decrementStock(request.getProductId());

        if(updatedRows == 0){
            throw new OutOfStockException("Product out of Stock");
        }

        return PurchaseResponse.builder()
                .message("Purchase Successful")
                .build();
    }
}
