package com.syamsundar.product_service.messaging.producer;

import com.syamsundar.product_service.messaging.dto.StockDecrementEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StockEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(UUID productId, UUID orderId, int quantity){

        StockDecrementEvent event =
                new StockDecrementEvent(
                        UUID.randomUUID(),
                        orderId,
                        productId,
                        quantity,
                        Instant.now()
                );
        kafkaTemplate.send(
                "stock-decrement-topic",
                productId.toString(),
                event
        );
    }
}
