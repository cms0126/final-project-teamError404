package com.error404.geulbut.jpa.introduction.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IntroductionDto {
    private String imgUrl;       // 이미지 경로 (IMG_URL)
    private String title;        // 도서 제목 (TITLE)
    private String name;       // 작가 PK (AUTHOR_ID)
    private LocalDate publishedDate;  // 출판일 (PUBLISHED_DATE)
    private String description;  // 설명 (DESCRIPTION)

    private Long bookId;


    public String getImageUrl() {return imgUrl;}
}
