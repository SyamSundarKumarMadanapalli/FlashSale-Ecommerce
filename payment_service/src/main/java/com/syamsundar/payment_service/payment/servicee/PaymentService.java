package com.syamsundar.payment_service.payment.servicee;

import com.syamsundar.payment_service.payment.PaymentStatus;
import com.syamsundar.payment_service.payment.dto.PaymentRequest;
import com.syamsundar.payment_service.payment.dto.PaymentResponse;
import com.syamsundar.payment_service.payment.entity.Payment;
import com.syamsundar.payment_service.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentResponse processPayment(PaymentRequest request) {

        Payment payment = new Payment();

        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());

        boolean success = Math.random() > 0.3;

        if(success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            return PaymentResponse.builder()
                    .success(true)
                    .message("Payment Successfull")
                    .build();
        }

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        return PaymentResponse.builder()
                .success(false)
                .message("Payment Failed")
                .build();
    }
}
