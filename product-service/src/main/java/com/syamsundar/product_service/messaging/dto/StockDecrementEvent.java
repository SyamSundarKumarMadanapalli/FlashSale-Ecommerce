package com.syamsundar.product_service.messaging.dto;

import java.time.Instant;
import java.util.UUID;

public record StockDecrementEvent(
        UUID eventId,
        UUID orderId,
        UUID productId,
        int quantity,
        Instant createdAt
) {
}
