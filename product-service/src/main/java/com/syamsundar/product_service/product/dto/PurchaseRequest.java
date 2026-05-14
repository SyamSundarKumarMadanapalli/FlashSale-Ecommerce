package com.syamsundar.product_service.product.dto;

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
}
