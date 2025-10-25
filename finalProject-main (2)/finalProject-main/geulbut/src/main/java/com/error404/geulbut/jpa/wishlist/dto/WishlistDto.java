package com.error404.geulbut.jpa.wishlist.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistDto {
    private String wishlistId;
    private String userId;
    private Long bookId;

    //  BOOKS 테이블에서 join해서 가져올 값
    private String title;
    private String authorName;
    private String publisherName;
    private String imgUrl;
    private Long price;
    private Long discountedPrice;
    private Long wishCount;

    private LocalDateTime createdAt;
}
