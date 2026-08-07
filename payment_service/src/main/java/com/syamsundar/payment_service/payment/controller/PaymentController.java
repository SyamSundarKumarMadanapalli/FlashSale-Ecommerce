package com.syamsundar.payment_service.payment.controller;

import com.syamsundar.payment_service.payment.dto.PaymentRequest;
import com.syamsundar.payment_service.payment.dto.PaymentResponse;
import com.syamsundar.payment_service.payment.entity.Payment;
import com.syamsundar.payment_service.payment.servicee.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse pay(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }
}
