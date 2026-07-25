package com.syamsundar.product_service.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.syamsundar.product_service.messaging.dto.StockDecrementEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    ProducerFactory<String, StockDecrementEvent> stockEventProducerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new DefaultKafkaProducerFactory<>(
                Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers),
                new StringSerializer(),
                new JsonSerializer<>(objectMapper)
        );
    }

    @Bean
    KafkaTemplate<String, StockDecrementEvent> kafkaTemplate(
            ProducerFactory<String, StockDecrementEvent> stockEventProducerFactory) {
        return new KafkaTemplate<>(stockEventProducerFactory);
    }
}
