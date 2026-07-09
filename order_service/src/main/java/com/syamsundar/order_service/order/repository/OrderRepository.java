package com.syamsundar.order_service.order.repository;

import com.syamsundar.order_service.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

}
