package com.syamsundar.order_service.order.dto;

import com.syamsundar.order_service.order.OrderStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private UUID id;

    private UUID productId;

    private Integer quantity;

    private OrderStatus status;
}
