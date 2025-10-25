package com.error404.geulbut.jpa.orders.controller;

import com.error404.geulbut.jpa.orders.dto.OrdersDto;
import com.error404.geulbut.jpa.orders.service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import static com.error404.geulbut.jpa.orders.entity.Orders.STATUS_CANCELLED;
import static com.error404.geulbut.jpa.orders.entity.Orders.STATUS_PAID;
import static com.error404.geulbut.jpa.orders.entity.Orders.STATUS_PENDING;
import static com.error404.geulbut.jpa.orders.entity.Orders.STATUS_SHIPPED;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.Map;



@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrdersController {
    private final OrdersService ordersService;
//  허용 상태값 화이트리스트
    private static final Set<String> ALLOWED_STATUSES = Set.of(
            STATUS_PENDING, STATUS_PAID, STATUS_CANCELLED, STATUS_SHIPPED, "DELIVERED"
    );

    //    주문 생성
    @PostMapping
    public OrdersDto createOrder(@RequestBody OrdersDto dto,
                                 Authentication authentication,
                                 HttpServletRequest request) {
        String userId = authentication.getName();
        log.info(" [POST] 주문 생성 요청 - userId: {}", userId);

        dto.setUserId(userId);
        OrdersDto saved = ordersService.createOrder(dto);

//        마지막주문 ID 세션 저장
        request.getSession().setAttribute("lastOrderId", saved.getOrderId());

        return saved;
    }

    //  주문 조회
    //  주문 단건 조회( 본인 또는 어드민만)
    @GetMapping("/{orderId}")
    public OrdersDto getOrder(@PathVariable Long orderId,
                              Authentication authentication) {
        String caller = safeUserId(authentication);
        OrdersDto dto = ordersService.getOrder(orderId);

        if (!isOwnerOrAdmin(authentication, dto.getUserId())) {
            log.warn("[GET] 주문 조회 권한없음 - caller: {}, owner: {}, orderId: {}",
                    caller, dto.getUserId(), orderId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        log.info(" [GET] 주문 조회 - userId: {}, orderId: {}", caller, orderId);

        return dto;
    }

    //    주문 내역 조회
    //    내 주문 목록
    @GetMapping("/user")
    public List<OrdersDto> getUserOrders(Authentication authentication) {
        String userId = safeUserId(authentication);
        log.info(" [GET] 사용자 주문 내역 조회 - userId: {}", userId);

        return ordersService.getUserOrders(userId);
    }

//    주문 상태 변경
    @PatchMapping("/{orderId}/status")
    public OrdersDto updateOrderStatus(@PathVariable Long orderId,
                                       @RequestParam String status,
                                       Authentication authentication){

        String caller = safeUserId(authentication);

        //  상태 정규화 / 검증
        String normalized = normalizeStatus(status);
        if (!ALLOWED_STATUSES.contains(normalized)) {
            log.warn("[PATCH] 잘못된 상태값 - caller: {}, orderId: {}, status: {}",
                    caller, orderId, status);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "허용되지 않는 상태값입니다.");
        }

        //  소유자 또는 어드민만 허용
        OrdersDto current = ordersService.getOrder(orderId);
        if (!isOwnerOrAdmin(authentication, current.getUserId())) {
            log.warn("[PATCH] 주문 상태 변경 권한없음 - caller: {}, owner: {}, orderId: {}, status: {}",
                    caller, current.getUserId(), orderId, normalized);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        log.info("[PATCH] 주문 상태 변경 - userId: {}, orderId: {}, status: {}", caller, orderId, normalized);
        return ordersService.updateOrderStatus(orderId, normalized);
    }

    // ===== 헬퍼 =====
    private String safeUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return authentication.getName();
    }

    private boolean isOwnerOrAdmin(Authentication auth, String ownerUserId) {
        if (auth == null) return false;
        String caller = auth.getName();
        if (caller != null && caller.equals(ownerUserId)) return true;
        return hasRole(auth, "ROLE_ADMIN");
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities() != null &&
                auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(role::equals);
    }

    private String normalizeStatus(String s) {
        return s == null ? null : s.trim().toUpperCase();

    }

//    주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId,
                            Authentication authentication){
        String userId = authentication.getName();
        log.info("[DELETE] 주문 삭제 - userId: {}, orderID: {}", userId, orderId);

        try {
            ordersService.deleteOrder(orderId, userId);
            return ResponseEntity.ok(Map.of("status", "ok", "message", "주문이 삭제되었습니다."));
        } catch (Exception e) {
            log.error("❌ 주문 삭제 실패 - userId: {}, orderId: {}", userId, orderId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "fail", "message", e.getMessage()));
        }
    }
}