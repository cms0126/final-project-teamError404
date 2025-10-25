package com.error404.geulbut.jpa.choice.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChoiceDto {
    private Long bookId;
    private String imgUrl;       // 이미지 경로 (IMG_URL)
    private String title;        // 도서 제목 (TITLE)
    private String name;       // 작가 PK (AUTHOR_ID)
    private String description;  // 설명 (DESCRIPTION)
}
