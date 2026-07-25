package com.syamsundar.product_service.messaging.consumer;
import com.syamsundar.product_service.messaging.dto.StockDecrementEvent;
import com.syamsundar.product_service.processedEvent.entity.ProcessedEvent;
import com.syamsundar.product_service.processedEvent.repository.ProcessedEventRepository;
import com.syamsundar.product_service.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
public class StockEventConsumer {

    private final ProductRepository productRepository;
    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    @KafkaListener(
            topics = "stock-decrement-topic",
            groupId = "product-group"
    )

    public void consume(StockDecrementEvent event){

        if(processedEventRepository.existsById(event.eventId())){
            return;
        }

        productRepository.decrementStock(event.productId(), event.quantity());

        processedEventRepository.save(
                new ProcessedEvent(
                        event.eventId(),
                        event.createdAt()
                )
        );
    }
}
