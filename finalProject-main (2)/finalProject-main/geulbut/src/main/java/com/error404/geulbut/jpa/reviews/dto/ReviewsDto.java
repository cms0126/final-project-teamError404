package com.error404.geulbut.jpa.reviews.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewsDto {
    private Long reviewId;          // PK, DB에서 시퀀스로 자동 생성
    private Long bookId;
    private String userId;
    private Integer rating;
    private String content;
    private Long orderedItemId;
    // 리뷰 대상 주문 아이템
}