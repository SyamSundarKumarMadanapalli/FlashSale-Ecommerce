package com.syamsundar.product_service.product.repository;

import com.syamsundar.product_service.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsByName(String name);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Product p
            SET p.availableStock = p.availableStock - 1
            WHERE p.id = :productId
            AND p.availableStock > 0
        """)
    int decrementStock(UUID productId);
}
