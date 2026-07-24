package com.syamsundar.product_service.processedEvent.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
@AllArgsConstructor
public class ProcessedEvent {

    @Id
    private UUID eventId;

    private Instant createdAt;
}
