package com.error404.geulbut.jpa.carts.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CartDto {
    private Long cartId;
    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private int price;
    private Long discountedPrice;
    private String imgUrl;
    private int quantity;
    private Long totalPrice;    // 총액 (price * quantity)
}
