package com.error404.geulbut.jpa.hotdeal.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

//이미지  IMG_URL
//도서제목 title
//작가명 AUTHOR_ID(참조키)AUTHORS
//할인 PRICE
//가격 DISCOUNTED_PRICE

public class HotdealDto {
    private Long bookId;
    private String imgUrl;       // 이미지 경로 (IMG_URL)
    private String title;        // 도서 제목 (TITLE)
    private String name;       // 작가 PK (AUTHOR_ID)
    private Long price;        //할인
    private Long discounted_price;    // 가격
}
