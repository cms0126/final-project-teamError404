package com.error404.geulbut.jpa.orderitem.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private Long orderedItemId;
    private Long bookId;
    private int quantity;
    private Long price;
    private Long discountedPrice;
    private String title;
    private String imageUrl;
}
