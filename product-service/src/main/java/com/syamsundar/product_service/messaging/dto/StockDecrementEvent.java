package com.syamsundar.product_service.messaging.dto;

import java.util.UUID;

public record StockDecrementEvent (
    UUID productId,
    int quantity
){}
