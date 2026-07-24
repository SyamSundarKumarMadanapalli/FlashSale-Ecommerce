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

import java.time.Duration;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StockEventProducer stockEventProducer;
    private final ProductCacheService productCacheService;

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

    public ProductResponse getProductWithStock(UUID productId) {

        ProductResponse response = productCacheService.getProduct(productId);
        Object stockObj = redisTemplate.opsForValue()
                .get("stock:" + productId);

        if (stockObj != null) {
            response.setAvailableStock(
                    Integer.parseInt(stockObj.toString())
            );
        }

        return response;
    }

    @Transactional
    public PurchaseResponse purchaseProduct(PurchaseRequest request){

        boolean success =
                decrementStock(request);

        if (!success) {
            return PurchaseResponse.builder()
                    .message("Stock unavailable")
                    .build();
//            throw new OutOfStockException(
//                    "Product out of Stock"
//            );
        }

        return PurchaseResponse.builder()
                .message("Purchase Successful")
                .build();
    }


    @Transactional
    public boolean decrementStock(PurchaseRequest request) {
        String redisKey = "stock:" + request.getProductId();
        Integer quantity = request.getQuantity();

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        if(Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))){
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));

            if (product.getAvailableStock() < quantity) {
                return false;
            }

            redisTemplate.opsForValue()
                    .set(redisKey, product.getAvailableStock());

            redisTemplate.expire(redisKey, Duration.ofDays(3));
        }

        Long stock = redisTemplate.opsForValue().decrement(redisKey, quantity);

        if (stock == null) {
            return false;
        }

        if (stock < 0) {
            redisTemplate.opsForValue().increment(redisKey, quantity);
            return false;
        }

        stockEventProducer.publish(request.getProductId(), quantity);

        return true;
    }
}
