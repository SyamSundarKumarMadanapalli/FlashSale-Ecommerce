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
    private final KafkaTemplate<String, StockDecrementEvent> kafkaTemplate;

    public void publish(UUID productId, int quantity){

        kafkaTemplate.send(
                "stock-decrement-topic",
                new StockDecrementEvent(UUID.randomUUID(),
                        UUID.randomUUID(),
                        productId,
                        quantity,
                        Instant.now()
                )
        );
    }
}
