package com.error404.geulbut.jpa.carts.controller;

import com.error404.geulbut.jpa.carts.service.CartService;
import com.error404.geulbut.jpa.carts.service.PaymentService;
import com.error404.geulbut.jpa.orders.entity.Orders;
import com.error404.geulbut.jpa.orders.repository.OrdersRepository;
import com.error404.geulbut.jpa.users.entity.Users;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final CartService cartService;
    private final OrdersRepository ordersRepository;
    private final PaymentService paymentService;

    @PostMapping("/prepare")
    public Map<String, String> prepare(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String merchantUid = "order_" + System.currentTimeMillis();
        return Map.of("merchantUid", merchantUid);
    }

    public record VerifyReq(
            @NotBlank String impUid,
            @NotBlank String merchantUid,
            OrdersInfo ordersInfo
    ) {
        public record OrdersInfo(
                String userId,
                String recipient,
                String phone,
                String address,
                String memo,
                String payMethod,
                Long amount,
                Long bookId,
                Integer quantity,
                String mode
        ) {}
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verify(Authentication authentication,
                                    @Valid @RequestBody VerifyReq req) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = authentication.getName();

//        1) 우리측 서버 결제 금액과 PG측 결제 금액 조회
        String token = paymentService.getAccessToken();
        Map<String, Object> paymentInfo = paymentService.verifyPayment(token, req.impUid());
        Long paidAmount = ((Number) paymentInfo.get("amount")).longValue();

        VerifyReq.OrdersInfo info = req.ordersInfo();
        if (info == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "ordersInfo is required"));
        }
//        2) OrderInfo.amount vs paidAmount(PG서버) 비교
//              금액 변조 방지 교차검증
        if (!paidAmount.equals(info.amount())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "amount mismatch"));
        }

        //  멱등(선조회) 체크 + 사용자 일치 확인
        Orders existing = ordersRepository.findByMerchantUid(req.merchantUid()).orElse(null);
        if (existing != null) {
            if (!userId.equals(existing.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "owner mismatch"));
            }
            return ResponseEntity.ok(Map.of(
                    "orderId", existing.getOrderId(),
                    "total", existing.getTotalPrice(),
                    "status", existing.getStatus(),
                    "idempotent", true
            ));
        }

        //  신규 주문 생성
        try {
            Orders order;
            String mode = info.mode();
            if ("BUY_NOW".equalsIgnoreCase(mode)) {
                long bookId = (info.bookId() == null ? 0L : info.bookId());
                int qty    = (info.quantity() == null || info.quantity() <= 0) ? 1 : info.quantity();

                //  바로구매 전용 서비스 메서드 (CartService에 구현 필요)
                //   "장바구니에 담지 않고 단건으로 주문아이템 구성" 로직
                order = cartService.checkoutBuyNow(userId, req.merchantUid(), bookId, qty, info.amount());
            } else {
                //  장바구니 기반 결제 (기존 로직)
                //   CartService에 "장바구니→주문 생성" 메서드가 따로 있다면 그걸 호출
                order = cartService.checkoutFromCart(userId, req.merchantUid(), info.amount());
                // 기존에 cartService.checkout(userId, req.merchantUid(), info) 사용 중이면 그대로 둬도 OK
            }

            order.setImpUid(req.impUid());
            ordersRepository.save(order);

            // 주문 수 증가 처리
            paymentService.processOrder(req.impUid());

            return ResponseEntity.ok(Map.of(
                    "orderId", order.getOrderId(),
                    "total", order.getTotalPrice(),
                    "status", order.getStatus(), // 'PAID' 기대
                    "idempotent", false
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (DataIntegrityViolationException race) {
            log.warn("Idempotency race: {}", race.getMessage());
            Orders after = ordersRepository.findByMerchantUid(req.merchantUid()).orElse(null);
            if (after != null) {
                return ResponseEntity.ok(Map.of(
                        "orderId", after.getOrderId(),
                        "total", after.getTotalPrice(),
                        "status", after.getStatus(),
                        "idempotent", true
                ));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "duplicate merchantUid"));
        }
    }
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason,
            Authentication authentication){

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = authentication.getName();

        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주문없음"));

        if (!userId.equals(order.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "owner mismatch"));
        }
        // 이미 취소된 주문은 다시 취소할 필요 없음
        if (Orders.STATUS_CANCELLED.equals(order.getStatus())) {
            return ResponseEntity.ok(Map.of("status", "already_cancelled", "message", "이미 취소된 주문입니다."));
        }

        try {
            String token = paymentService.getAccessToken();

            // 1) PG 상태 확인
            Map<String, Object> payInfo = paymentService.verifyPayment(token, order.getImpUid());
            String pgStatus = (String) payInfo.get("status");
            if (!"paid".equalsIgnoreCase(pgStatus)) {
                order.setStatus(Orders.STATUS_CANCELLED);
                ordersRepository.save(order);
                return ResponseEntity.ok(Map.of(
                        "status", "synced",
                        "message", "PG가 paid 상태가 아니라서 DB만 취소 상태로 동기화했습니다."
                ));
            }

            Map<String, Object> cancelRes = paymentService.cancelPayment(
                    token,
                    order.getImpUid(),
                    reason
            );

            order.setStatus(Orders.STATUS_CANCELLED);
            ordersRepository.save(order);

            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "message", "결제가 정상적으로 취소되었습니다.",
                    "cancel", cancelRes
            ));
        } catch (Exception e){
            log.error("결제 취소실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "결제 취소 실패: " + e.getMessage()));
        }
    }
}
