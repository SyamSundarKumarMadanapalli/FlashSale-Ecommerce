package com.syamsundar.product_service.kafka.producer;

import com.syamsundar.product_service.kafka.event.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockReservedProducer {

    private static final String TOPIC = "StockReserved";

    private final KafkaTemplate<String, StockReservedEvent> kafkaTemplate;

    public void publish(StockReservedEvent stockReservedEvent) {

        kafkaTemplate.send(
                TOPIC,
                stockReservedEvent.getOrderId().toString(),
                stockReservedEvent
        );
    }
}
