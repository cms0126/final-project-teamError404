package com.error404.geulbut.jpa.books.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookApiDto {
    private String title;       // 제목
    private String author;      // 저자
    private String publisher;   // 발행자
    private String pubYear;     // 발행년도 (예: "2016")
    private String imageUrl;    // 표지 이미지
    private String description; // 책 소개
}
