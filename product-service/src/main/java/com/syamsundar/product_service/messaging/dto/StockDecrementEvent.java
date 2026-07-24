package com.syamsundar.product_service.messaging.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
public class StockDecrementEvent {
    public UUID eventId;
    public UUID orderId;
    public UUID productId;
    public int quantity;
    public Instant createdAt;
}
