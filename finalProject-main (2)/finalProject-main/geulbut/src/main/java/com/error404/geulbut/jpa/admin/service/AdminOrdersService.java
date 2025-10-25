package com.error404.geulbut.jpa.admin.service;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.orderitem.entity.OrderItem;
import com.error404.geulbut.jpa.orders.dto.OrdersDto;
import com.error404.geulbut.jpa.orders.entity.Orders;
import com.error404.geulbut.jpa.orders.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrdersService {

    private final OrdersRepository ordersRepository;
    private final MapStruct mapStruct;
    private final ErrorMsg errorMsg;

    // 전체 주문 조회(페이징)
    public Page<OrdersDto> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ordersRepository.findAll(pageable)
                .map(order -> {
                    OrdersDto dto = mapStruct.toDto(order);
                    dto.setUserName(order.getUser() != null ? order.getUser().getName() : null);
                    return dto;
                });
    }

    // 단일 주문 조회(DTO 반환)
    public OrdersDto getOrderById(Long orderId) {
        Orders order = getOrderByIdEntity(orderId);
        return mapToDto(order);
    }

    // 단일 주문 조회(엔티티 반환, Controller용)
    public Orders getOrderByIdEntity(Long orderId) {
        return ordersRepository.findWithItemsAndBooksByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException(errorMsg.getMessage("error.orders.notfound")));
    }

    // Orders -> OrdersDto 변환(추가 정보 포함)
    public OrdersDto mapToDto(Orders order) {
        OrdersDto dto = mapStruct.toDto(order);

        // 유저 정보
        if (order.getUser() != null) {
            dto.setUserName(order.getUser().getName());
            dto.setPhone(order.getUser().getPhone());
        }

        // 주문 정보
        dto.setAddress(order.getAddress());
        dto.setRecipient(order.getRecipient());
        dto.setMemo(order.getMemo());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaidAt(order.getPaidAt());
        dto.setDeliveredAt(order.getDeliveredAt());
        dto.setMerchantUid(order.getMerchantUid());
        dto.setCreatedAt(order.getCreatedAt() != null
                ? order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : null);

        return dto;
    }

    // 전체 주문 조회(관리자용, items + books 포함, 필터 + 페이징)
    public Page<OrdersDto> getAllOrdersWithItems(String userId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Repository에서 List로 가져오기 때문에 수동 페이징 처리
        List<Orders> orders = ordersRepository.findAllWithItemsAndBooks(
                userId != null && !userId.isEmpty() ? userId : null,
                status != null && !status.isEmpty() ? status : null
        );

        // List -> DTO 변환
        List<OrdersDto> dtos = orders.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        // 수동 페이징
        int start = Math.min((int) pageable.getOffset(), dtos.size());
        int end = Math.min(start + pageable.getPageSize(), dtos.size());
        List<OrdersDto> pagedList = dtos.subList(start, end);

        return new PageImpl<>(pagedList, pageable, dtos.size());
    }

    // 상태 변경
    @Transactional
    public OrdersDto updateOrderStatus(Long orderId, String newStatus) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.setStatus(newStatus);

        // 배송완료이면 주문일 기준 + 3일로 deliveredAt 설정
        if ("DELIVERED".equalsIgnoreCase(newStatus)) {
            if (order.getDeliveredAt() == null) {
                LocalDateTime base = order.getCreatedAt() != null ? order.getCreatedAt() : LocalDateTime.now();
                order.setDeliveredAt(base.plusDays(3));
            }
        } else {
            order.setDeliveredAt(null); // DELIVERED 외 상태면 초기화
        }

        Orders saved = ordersRepository.save(order);
        return mapToDto(saved);
    }

    // 신규 주문 생성
    public OrdersDto createOrder(OrdersDto dto) {
        LocalDateTime now = LocalDateTime.now();

        Orders order = Orders.builder()
                .userId(dto.getUserId())
                .totalPrice(dto.getTotalPrice())
                .status(Orders.STATUS_PENDING)
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .memo(dto.getMemo())
                .paymentMethod(dto.getPaymentMethod())
                .recipient(dto.getRecipient())
                .merchantUid(dto.getMerchantUid())
                .paidAt(now)
                .deliveredAt(now.plusDays(3)) // 신규 주문 생성 시 배송일 +3일
                .build();

        if (dto.getItems() != null) {
            dto.getItems().forEach(itemDto -> {
                OrderItem item = mapStruct.toEntity(itemDto);
                order.addItem(item);
            });
        }

        Orders saved = ordersRepository.save(order);
        return mapToDto(saved);
    }
}
