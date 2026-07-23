package com.syamsundar.order_service.product.client;

import com.syamsundar.order_service.product.dto.PurchaseRequest;
import com.syamsundar.order_service.product.dto.PurchaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductClient {

    private final WebClient webClient;

    public PurchaseResponse purchaseProduct(PurchaseRequest request){
        return webClient
                .post()
                .uri("/api/products/purchase")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PurchaseResponse.class)
                .block();
    }
}
