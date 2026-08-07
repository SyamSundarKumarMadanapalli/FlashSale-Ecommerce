package com.syamsundar.payment_service.payment.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    private UUID orderId;

    private UUID userId;

    private Double amount;
}
