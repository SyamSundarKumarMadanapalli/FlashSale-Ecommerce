package com.syamsundar.product_service.processedEvent.repository;

import com.syamsundar.product_service.processedEvent.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {
    boolean existsById(UUID eventId);
}
