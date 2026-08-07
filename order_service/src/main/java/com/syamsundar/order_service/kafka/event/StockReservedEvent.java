package com.syamsundar.order_service.kafka.event;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReservedEvent {

    private UUID orderId;

    private UUID productId;

    private UUID userId;

    private Integer quantity;

    private Double amount;
}
