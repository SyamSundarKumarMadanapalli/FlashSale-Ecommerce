package com.syamsundar.product_service.product.service;

import com.syamsundar.product_service.common.exception.OutOfStockException;
import com.syamsundar.product_service.common.exception.ProductAlreadyExistsException;
import com.syamsundar.product_service.common.exception.ProductNotFoundException;
import com.syamsundar.product_service.messaging.producer.StockEventProducer;
import com.syamsundar.product_service.product.dto.CreateProductRequest;
import com.syamsundar.product_service.product.dto.ProductResponse;
import com.syamsundar.product_service.product.dto.PurchaseRequest;
import com.syamsundar.product_service.product.dto.PurchaseResponse;
import com.syamsundar.product_service.product.entity.Product;
import com.syamsundar.product_service.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StockEventProducer stockEventProducer;

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

        String redisKey = "stock:" + savedProduct.getId();
        redisTemplate.opsForValue().set(
                redisKey,
                savedProduct.getAvailableStock()
        );
        redisTemplate.expire(redisKey, java.time.Duration.ofDays(3));

        return ProductResponse.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .price(savedProduct.getPrice())
                .availableStock(savedProduct.getAvailableStock())
                .build();
    }

    @Cacheable(value = "products", key = "#productId")
    public ProductResponse getProduct(UUID productId){
        System.out.println("Fetching product from DB...----------------------------------------------------------------");
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

        boolean success =
                decrementStock(request.getProductId());

        if (!success) {
            throw new OutOfStockException(
                    "Product out of Stock"
            );
        }

        return PurchaseResponse.builder()
                .message("Purchase Successful")
                .build();
    }


    @Transactional
    public boolean decrementStock(UUID productId) {
        String redisKey = "stock:" + productId;

        Long stock = redisTemplate.opsForValue().decrement(redisKey);
        if (stock == null) {

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));

            int currentDbStock = product.getAvailableStock();

            if (currentDbStock <= 0) {
                return false;
            }

            redisTemplate.opsForValue().set(redisKey, currentDbStock);
            redisTemplate.expire(redisKey, java.time.Duration.ofDays(3));

            stock = redisTemplate.opsForValue().decrement(redisKey);
        }

        if (stock < 0) {
            redisTemplate.opsForValue().increment(redisKey);
            return false;
        }

        stockEventProducer.publish(productId);
        return true;
    }
}
