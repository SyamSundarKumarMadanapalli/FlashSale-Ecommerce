package com.syamsundar.product_service.kafka.event;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReservedEvent {

    private UUID eventId;

    private UUID orderId;

    private UUID productId;

    private UUID userId;

    private Integer quantity;

    private Instant timestamp;
}
