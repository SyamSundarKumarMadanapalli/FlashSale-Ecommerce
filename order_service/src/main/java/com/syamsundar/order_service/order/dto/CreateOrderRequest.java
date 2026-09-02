package com.syamsundar.order_service.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull
    private UUID productId;

    private UUID userId;

    @Min(1)
    private Integer quantity;

    private Double amount;
}
