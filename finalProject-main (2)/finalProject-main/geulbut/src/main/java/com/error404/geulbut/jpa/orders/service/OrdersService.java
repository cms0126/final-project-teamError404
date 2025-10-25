package com.error404.geulbut.jpa.orders.service;

import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.books.repository.BooksRepository;
import com.error404.geulbut.jpa.orders.dto.OrdersDto;
import com.error404.geulbut.jpa.orders.entity.Orders;
import com.error404.geulbut.jpa.orders.repository.OrdersRepository;
import com.error404.geulbut.jpa.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static com.error404.geulbut.jpa.orders.entity.Orders.STATUS_PENDING;
import static com.error404.geulbut.jpa.orders.entity.Orders.STATUS_PAID;

@Service
@RequiredArgsConstructor
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final MapStruct mapStruct;
    private final BooksRepository booksRepository;
    private final UsersService usersService;                // 추가 ( 덕규)
    private final Clock clock;
    private final PointService pointService;

    private static final DateTimeFormatter DELIVERY_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd (E) HH:mm");

    @Transactional
    public OrdersDto createOrder(OrdersDto dto) {
        Orders order = new Orders();
        order.setUserId(dto.getUserId());
        order.setTotalPrice(dto.getTotalPrice());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setAddress(dto.getAddress());
        order.setStatus(STATUS_PENDING);             // PENDING -> STATUS_PENDING (덕규:문자열 대신 상수)
//        order.setStatus("PAID"); 주석으로 잠시 막아놓음!--덕규

//        아이템 NPE 방어 로직
        var items = dto.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("주문 아이템이 비어있습니다.");
        }
        // items NPE 방어 (엔티티에서 미초기화시)
        if (order.getItems() == null) {
            order.setItems(new java.util.ArrayList<>());
        }


        dto.getItems().forEach(itemDto -> {
            Long bookId = itemDto.getBookId();
            if (bookId == null) {
                throw new IllegalArgumentException("bookId가 없습니다.");
            }

            Books book = booksRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 도서를 찾을 수 없습니다. id=" + bookId));

            // OrderItem 엔티티 만들어서 채워주기
            var orderItem = mapStruct.toEntity(itemDto);
            orderItem.setOrder(order);
            orderItem.setBook(book);

            order.getItems().add(orderItem);
        });

        Orders savedOrder = ordersRepository.save(order);
        Orders reloadedOrder = ordersRepository.findWithItemsAndBooksByOrderId(savedOrder.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문 재조회 실패 id=" + savedOrder.getOrderId()));
        return mapStruct.toDto(reloadedOrder);
    }

    @Transactional(readOnly = true)
    public OrdersDto getOrder(Long orderId) {
        Orders order = ordersRepository.findWithItemsAndBooksByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다. id=" + orderId));
        return mapStruct.toDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrdersDto> getUserOrders(String userId) {
        List<Orders> orders = ordersRepository.findWithItemsAndBooksByUserId(userId);
        return orders.stream()
                .map(mapStruct::toDto)
                .toList();
    }


    //    주문 상태 변경 (예: PENDING -> PAID -> SHIPPED/CANCELLED)
    @Transactional // 추가:덕규

    public OrdersDto updateOrderStatus(Long orderId, String newStatus) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없음. id=" + orderId));

        String oldStatus = nvl(order.getStatus());

//     배송완료 시각 기록 (DB에 DELIVERED_AT 하나만 추가하는 최소 설계)
        if ("DELIVERED".equalsIgnoreCase(newStatus)) {
            if (order.getDeliveredAt() == null) {
                order.setDeliveredAt(java.time.LocalDateTime.now());
            }
        }

        // 1) 상태 저장
        order.setStatus(newStatus);
        Orders updateOrder = ordersRepository.save(order);

        // 2) 등급/누적 처리
        long amount = nz(order.getTotalPrice());

        // 펜딩/다른상태 -> paid : 누적증가
        if (!STATUS_PAID.equalsIgnoreCase(oldStatus) && STATUS_PAID.equalsIgnoreCase(newStatus)) {
            usersService.addPurchaseAndRegrade(updateOrder.getUserId(), amount);
        }
        // PAID -> (PAID 외) : 환불/취소로 간주하여 누적 감소
        else if (STATUS_PAID.equalsIgnoreCase(oldStatus) && !STATUS_PAID.equalsIgnoreCase(newStatus)) {
            usersService.refundAndRegrade(updateOrder.getUserId(), amount);
        }

        // 포인트 적립/회수
        if (!STATUS_PAID.equalsIgnoreCase(oldStatus) && STATUS_PAID.equalsIgnoreCase(newStatus)) {
            // 결제완료로 전환 → 포인트 적립
            pointService.accrueOnPaidOrder(orderId);
        } else if (STATUS_PAID.equalsIgnoreCase(oldStatus) && !STATUS_PAID.equalsIgnoreCase(newStatus)) {
            // 결제완료였다가 다른 상태(취소/환불 등)로 전환 → 포인트 회수
            pointService.revokeOnCancelledOrder(orderId);
        }

        return mapStruct.toDto(updateOrder);
    }


    private static long nz(Long v) {
        return v == null ? 0L : v;
    }

    private static String nvl(String s) {
        return (s == null) ? "" : s;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();}

    //    주문 삭제
    @Transactional
    public void deleteOrder(Long orderId, String userId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // 🔐 보안: 자기 주문만 삭제 가능
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("본인 주문만 삭제할 수 있습니다.");
        }

        ordersRepository.delete(order);
    }

    //    배송조회 페이지 구현
    public enum ViewDeliveryStatus {READY, IN_TRANSIT, DELIVERED}

    public static class DeliveryView {
        private final ViewDeliveryStatus viewDeliveryStatus;
        private final OrdersDto ordersDto;

        public DeliveryView(ViewDeliveryStatus viewDeliveryStatus, OrdersDto ordersDto) {
            this.viewDeliveryStatus = viewDeliveryStatus;
            this.ordersDto = ordersDto;
        }
        public ViewDeliveryStatus getViewDeliveryStatus() {return viewDeliveryStatus;}
        public OrdersDto getOrdersDto() {return ordersDto;}

        public String getViewDeliveryStatusName() {
            return viewDeliveryStatus == null ? "" : viewDeliveryStatus.name();
        }
}
@Transactional(readOnly = true)
public DeliveryView buildDeliveryView (Long orderId) {
    OrdersDto ordersDto = getOrder(orderId);
    ViewDeliveryStatus vs = resolveViewStatus(ordersDto);

    applySimulatedDeliveredIfNeeded (ordersDto, vs);

    return new DeliveryView(vs, ordersDto);
    }
private ViewDeliveryStatus resolveViewStatus(OrdersDto o) {
    if ("DELIVERED".equalsIgnoreCase(o.getStatus()) || o.getDeliveredAt() != null) {
        return ViewDeliveryStatus.DELIVERED;
    }
    if ("SHIPPED".equalsIgnoreCase(o.getStatus())) {
        return ViewDeliveryStatus.IN_TRANSIT;
    }
    if (o.getPaidAt() != null) {
        LocalDateTime now =  LocalDateTime.now(clock);
        LocalDateTime d1 = o.getPaidAt().plusDays(1);
        LocalDateTime d3 = o.getPaidAt().plusDays(3);

        if(now.isBefore(d1)) return ViewDeliveryStatus.READY;
        if(now.isBefore(d3)) return ViewDeliveryStatus.IN_TRANSIT;
        return ViewDeliveryStatus.DELIVERED;
    }

        return ViewDeliveryStatus.READY;
    }

    private void applySimulatedDeliveredIfNeeded(OrdersDto o, ViewDeliveryStatus vs) {
        if (vs != ViewDeliveryStatus.DELIVERED) return;
        if (o.getDeliveredAt() != null) return;

        if (o.getPaidAt() != null) {
            o.setDeliveredAt(o.getPaidAt().plusDays(3));
        }
    }

    @Transactional(readOnly = true)
    public List<OrdersDto> getDeliveredHistory(String userId, int limit, Long excludeOrderId) {
        var page = ordersRepository.findDeliveredWithItemsAndBooksByUserId(
                userId, PageRequest.of(0, Math.max(1, limit))
        );
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd (E) HH:mm");

        return page.getContent().stream()
                .filter(o -> excludeOrderId == null || !o.getOrderId().equals(excludeOrderId)) // 현재 주문 제외
                .map(mapStruct::toDto)
                .peek(d -> {
                    if (d.getDeliveredAt() != null && (d.getDeliveredAtFormatted() == null || d.getDeliveredAtFormatted().isBlank())) {
                        d.setDeliveredAtFormatted(d.getDeliveredAt().format(fmt)); // 표시용 문자열 세팅
                    }
                })
                .toList();
    }
}



