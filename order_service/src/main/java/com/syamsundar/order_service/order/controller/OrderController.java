package com.syamsundar.order_service.order.controller;

import com.syamsundar.order_service.order.dto.CreateOrderRequest;
import com.syamsundar.order_service.order.dto.OrderResponse;
import com.syamsundar.order_service.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request){
        return orderService.createOrder(request);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable UUID orderId){
        return orderService.getOrder(orderId);
    }

    @GetMapping
    public List<OrderResponse> getAllOrders(){
        return orderService.getAllOrders();
    }
}
