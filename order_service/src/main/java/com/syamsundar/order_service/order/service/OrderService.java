package com.syamsundar.order_service.order.service;

import com.syamsundar.order_service.common.exception.OrderNotFoundException;
import com.syamsundar.order_service.order.OrderStatus;
import com.syamsundar.order_service.order.dto.CreateOrderRequest;
import com.syamsundar.order_service.order.dto.OrderResponse;
import com.syamsundar.order_service.order.entity.Order;
import com.syamsundar.order_service.order.repository.OrderRepository;
import com.syamsundar.order_service.product.client.ProductClient;
import com.syamsundar.order_service.product.dto.PurchaseRequest;
import com.syamsundar.order_service.product.dto.PurchaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();

        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setOrderStatus(OrderStatus.PENDING);

        order = orderRepository.save(order);

        PurchaseRequest purchaseRequest = new PurchaseRequest();

        purchaseRequest.setProductId(request.getProductId());
        purchaseRequest.setUserId(request.getUserId());
        purchaseRequest.setQuantity(request.getQuantity());

        PurchaseResponse purchaseResponse = productClient.purchaseProduct(purchaseRequest);

        if(purchaseResponse.isSuccess()){
            order.setOrderStatus(OrderStatus.STOCK_RESERVED);
        }else{
            order.setOrderStatus(OrderStatus.FAILED);
        }

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }


    public OrderResponse getOrder(UUID orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not Found"));

        return mapToResponse(order);
    }


    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> orderResponseList = new ArrayList<>();

        for (Order order : orders) {
            orderResponseList.add(mapToResponse(order));
        }

        return orderResponseList;
    }


    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .status(order.getOrderStatus())
                .build();
    }
}
