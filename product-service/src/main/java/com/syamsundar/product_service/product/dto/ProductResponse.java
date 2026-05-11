package com.syamsundar.product_service.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ProductResponse {

    private UUID id;
    private String name;
    private Double price;
    private Integer availableStock;
}
