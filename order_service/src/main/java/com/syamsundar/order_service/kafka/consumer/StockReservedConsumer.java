package com.syamsundar.order_service.kafka.consumer;

import com.syamsundar.order_service.kafka.event.StockReservedEvent;
import com.syamsundar.order_service.order.OrderStatus;
import com.syamsundar.order_service.order.entity.Order;
import com.syamsundar.order_service.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockReservedConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = "StockReserved",
            groupId = "order-service"
    )
    public void consume(StockReservedEvent event) {

        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++"+ event.getEventId() + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        log.info("Received stock reserved event: {}", event);

        Order order = orderRepository.findById(event.getOrderId())
                .orElse(null);

        if(order == null){
            log.error("Order with id {} not found", event.getOrderId());
            return;
        }

        order.setOrderStatus(OrderStatus.STOCK_RESERVED);

        orderRepository.save(order);

        log.info("Order with id {} has been updated to RESERVE_STOCK", order.getId());
    }
}
