package com.syamsundar.order_service.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PurchaseRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private UUID userId;

    @NotNull
    private UUID orderId;

    @Min(1)
    private Integer quantity;

    private Double amount;
}

