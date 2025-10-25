package com.error404.geulbut.jpa.orders.entity;

import com.error404.geulbut.common.BaseTimeEntity;
import com.error404.geulbut.jpa.orderitem.entity.OrderItem;
import com.error404.geulbut.jpa.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orders extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ORDERS")
    @SequenceGenerator(name = "SEQ_ORDERS", sequenceName = "SEQ_ORDERS", allocationSize = 1)
    private Long orderId;
    @Column(name = "USER_ID")
    private String userId;
    private Long totalPrice;
    private String status;

    private String phone;
    private String memo;
    private String paymentMethod;
    private String recipient;
    private String address;

//    포인트 관련 필드
    private Long pointsAccrued;                     // 이 주문으로 적립된 포인트
    private LocalDateTime pointsAccruedAt;    // 적립 시각
    private LocalDateTime pointsRevokedAt;   //  회수 시각(취소/환불시에)


//    결제 관련 필드
    @Column(name = "MERCHANT_UID", length = 100, unique = true)
    private String merchantUid;

    @Column(name = "PAID_AT")
    private LocalDateTime paidAt;

    @Column(name = "IMP_UID", unique = true)
    private String impUid;


    private LocalDateTime deliveredAt;
  


    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // 상태 상수 & 편의 메서드 -> 헬퍼 ( 덕규 )
    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_SHIPPED = "SHIPPED";
    public static final String STATUS_PENDING = "PENDING";

    public boolean isPaid() {
        return STATUS_PAID.equalsIgnoreCase(this.status);
    }

    public void markPaid() {
        this.status = STATUS_PAID;
    }

    public void cancel() {
        this.status = STATUS_CANCELLED;
    }

    @PrePersist
    public void prePersist() {
        if (this.status == null || this.status.isBlank()) {
            this.status = STATUS_CREATED;   // 기본값
        }
    }

    // 편의 메서드
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }


    public void markDelivered() {
        if (this.deliveredAt == null) this.deliveredAt = LocalDateTime.now();
    }
    
    // 기존 userId를 건드리지 않고 Users 엔티티와 연관
//    관리자 배송조회에서 이름 가져오기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private Users user;


}
