package com.error404.geulbut.jpa.orderitem.repository;

import com.error404.geulbut.jpa.orderitem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // OrderItem.order.orderId 기준으로 조회
    List<OrderItem> findByOrderOrderId(Long orderId);
}
