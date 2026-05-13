package com.syamsundar.product_service.product.repository;

import com.syamsundar.product_service.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsByName(String name);
}
