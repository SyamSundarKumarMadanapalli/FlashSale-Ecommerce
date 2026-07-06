package com.syamsundar.product_service.messaging.consumer;
import com.syamsundar.product_service.messaging.dto.StockDecrementEvent;
import com.syamsundar.product_service.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class StockEventConsumer {

    private final ProductRepository productRepository;

    @Transactional
    @KafkaListener(
            topics = "stock-decrement-topic",
            groupId = "product-group"
    )

    public void consume(StockDecrementEvent event){
        productRepository.decrementStock(event.productId());
    }
}
