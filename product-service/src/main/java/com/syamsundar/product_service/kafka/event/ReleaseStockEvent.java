package com.syamsundar.product_service.kafka.event;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReleaseStockEvent {

    private UUID productId;

    private Integer quantity;
}
