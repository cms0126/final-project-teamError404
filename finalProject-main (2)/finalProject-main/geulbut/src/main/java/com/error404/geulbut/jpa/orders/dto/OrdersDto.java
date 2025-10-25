package com.error404.geulbut.jpa.orders.dto;

import com.error404.geulbut.jpa.orderitem.dto.OrderItemDto;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersDto {
    private Long orderId;
    private String userId;
    private String userName;  // 추가
    private String status;
    private Long totalPrice;
    private String createdAt;

    private String phone;
    private String address;
    private String memo;
    private String paymentMethod;
    private String merchantUid;
    private List<OrderItemDto> items;

    private LocalDateTime paidAt;
    private LocalDateTime deliveredAt;

    //  배송 관련 추가
    private String invoiceNo;       // 송장번호
    private String courierName;     // 택배사
    private String courierManName;  // 기사명
    private String courierManPhone; // 기사 연락처
    private String recipient;       // 수취인
    private String deliveredAtFormatted;

    //    포인트 관련 추가
    private Long pointsAccrued;            // 이 주문으로 적립된 포인트
    private LocalDateTime pointsAccruedAt; // 적립 시각
    private LocalDateTime pointsRevokedAt; // 회수 시각

    public String getDeliveredAtFormatted() {
        return deliveredAt == null ? null
                : deliveredAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd (E) HH:mm"));
    }

    public String getPointsAccruedFormatted() {
        return pointsAccruedAt == null ? null
                : pointsAccruedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getPointsRevokedFormatted() {
        return pointsRevokedAt == null ? null
                : pointsRevokedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getCreatedAtFormatted() {
        if (createdAt == null || createdAt.isBlank()) return null;
        try {
            // 기본 ISO-8601 (예: 2025-09-30T14:05:33)
            LocalDateTime dt = LocalDateTime.parse(createdAt);
            return dt.format(DateTimeFormatter.ofPattern("yyyy-M-d HH:mm"));
        } catch (Exception e) {
            try {
                // DB에서 공백 있는 형태 (예: 2025-09-30 14:05:33)
                DateTimeFormatter inputFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dt = LocalDateTime.parse(createdAt, inputFmt);
                return dt.format(DateTimeFormatter.ofPattern("yyyy-M-d HH:mm"));
            } catch (Exception ignore) {
                return createdAt; // 그래도 안 되면 원본 반환
            }
        }
    }

//   deliveredAt을 화면에 포맷하기 위해서 씀 위에 있는건 E가 포함되어있는 날짜형태 'yyyy-M-d HH:mm' 통일
    public String getPaidAtFormatted() {
        return paidAt == null ? null
                : paidAt.format(DateTimeFormatter.ofPattern("yyyy-M-d HH:mm"));
    }
//  여기도 마찬가지
    public String getDeliveredAtFormattedShort() {
        return deliveredAt == null ? null
                : deliveredAt.format(DateTimeFormatter.ofPattern("yyyy-M-d HH:mm"));
    }


}

