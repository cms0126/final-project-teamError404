package com.error404.geulbut.jpa.orders.service;


import com.error404.geulbut.jpa.orders.entity.Orders;
import com.error404.geulbut.jpa.orders.repository.OrdersRepository;
import com.error404.geulbut.jpa.users.entity.Users;
import com.error404.geulbut.jpa.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

import static com.error404.geulbut.jpa.orders.entity.Orders.STATUS_PAID;
import static com.error404.geulbut.jpa.orders.entity.Orders.STATUS_CANCELLED;

@Service
@Log4j2
@RequiredArgsConstructor
public class PointService {
    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository;
    private final Clock clock;

//    적립비율 1%
    @Value("${app.point.earn-rate:0.01}")
    private double earnRate;

//    결제 완료 주문의 포인트 적립
    @Transactional
    public long accrueOnPaidOrder(Long orderId) {
        Orders orders = ordersRepository.findWithUserByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("order not found: " + orderId));

//        1) 상태 / 대상의 유효성파악
        if (!STATUS_PAID.equalsIgnoreCase(orders.getStatus())) {
            log.info("포인트 적립 스킵: status!=PAID (orderId={})", orderId);
            return 0L;
        }
        if (orders.getUserId() == null || orders.getUserId().isBlank()) {
            log.info("포인트 적립 스킵: 비회원 주문 (orderId={})", orderId);
            return 0L;
        }
//        이미 적립했으면?
        if (orders.getPointsAccrued() != null) {
            log.info("이미 적립됨(멱등) orderId={}, pointsAccrued={}", orderId, orders.getPointsAccrued());
            return 0L;
        }
//        적립액 계산
        long total = nvl(orders.getTotalPrice());
        long earn = (long) Math.floor(total * earnRate);
        if (earn <= 0) {
            log.info("포인트 적립 스킵: earn<=0 (orderId={}, total={}, rate={})", orderId, total, earnRate);
            return 0L;
        }
//        users 잔고 반영
        Users user = orders.getUser();
        if (user == null) {
            user = usersRepository.findById(orders.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("user not found: " + orders.getUserId()));
        }
        long curr = nvl(user.getPoint());
        user.setPoint(curr + earn);
        usersRepository.save(user);

//        orders에 적립 마킹(중복 방지 근거)
        orders.setPointsAccrued(earn);
        orders.setPointsAccruedAt(LocalDateTime.now(clock));
        ordersRepository.save(orders);

        log.info("포인트 적립 완료 orderId={}, userId={}, earn={}, newBalance={}",
                orderId, user.getUserId(), earn, user.getPoint());
        return earn;
    }

//    취소/환불된 주문의 포인트 회수
    @Transactional
    public long revokeOnCancelledOrder(Long orderId) {
        Orders order = ordersRepository.findWithUserByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("order not found: " + orderId));

//        1) 상태 대상 유효성
        if (!STATUS_CANCELLED.equalsIgnoreCase(order.getStatus())) {
            log.info("포인트 회수 스킵: status!=CANCELLED (orderId={})", orderId);
            return 0L;
        }
        Long accrued = order.getPointsAccrued();
        if (accrued == null) {
            log.info("포인트 회수 스킵: 해당 주문에 적립 이력이 없음 (orderId={})", orderId);
            return 0L;
        }
        if (order.getPointsRevokedAt() != null) {
            log.info("포인트 이미 회수됨(멱등) orderId={}", orderId);
            return 0L;
        }
        if (order.getUserId() == null || order.getUserId().isBlank()) {
            log.info("포인트 회수 스킵: 비회원 주문 (orderId={})", orderId);
            return 0L;
        }
//        2) 잔고 차감
        Users user = order.getUser();
        if (user == null) {
            user = usersRepository.findById(order.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("user not found: " + order.getUserId()));
        }
        long curr = nvl(user.getPoint());
        long newBal = Math.max(0L, curr - accrued);
        user.setPoint(newBal);
        usersRepository.save(user);

        //    3) ORDERS 에 회수 마킹
        order.setPointsRevokedAt(LocalDateTime.now(clock));
        ordersRepository.save(order);

        log.info("포인트 회수 완료 orderId={}, userId={}, revoke={}, newBalance={}",
                orderId, user.getUserId(), accrued, newBal);
        return accrued;
    }

//    미적립 paid 주문 n건 일괄 적립
    @Transactional
    public int accrueBatch(int limit) {
        var page = PageRequest.of(0, Math.max(1, limit));
        var list = ordersRepository.findPaidNotAccruedTop(page);
        int ok = 0;
        for (Orders o : list) {
            try {
                accrueOnPaidOrder(o.getOrderId());
                ok++;
            } catch (Exception e) {
                log.warn("accrueBatch 실패 orderId={}: {}", o.getOrderId(), e.getMessage());
            }
        }
        return ok;
    }

//    적립은 있는데 회수안된 캔슬 주문 건 일괄 회수
    @Transactional
    public  int revokeBatch(int limit) {
        var page = PageRequest.of(0, Math.max(1, limit));
        var list = ordersRepository.findCancelledNeedRevokeTop(page);
        int ok = 0;
        for (Orders o : list) {
            try {
                revokeOnCancelledOrder(o.getOrderId());
                ok++;
            } catch (Exception e) {
                log.warn("revokeBatch 실패 orderId={}: {}", o.getOrderId(), e.getMessage());
            }
        }
        return ok;
    }
    private static long nvl(Long v) { return v == null ? 0L : v; }
    }
