package com.syamsundar.order_service.order.entity;

import com.syamsundar.order_service.common.entity.BaseEntity;
import com.syamsundar.order_service.order.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
