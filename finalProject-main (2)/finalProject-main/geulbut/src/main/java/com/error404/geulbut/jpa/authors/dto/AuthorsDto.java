package com.error404.geulbut.jpa.authors.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "authorId")
public class AuthorsDto {
    private Long authorId;
    private String name;
    private String description;
    private String imgUrl;
    private LocalDateTime createdAt;


    public AuthorsDto(Long authorId, String name, String description) {
        this.authorId = authorId;
        this.name = name;
        this.description = description;
    }
//  등록날짜 통일을 위해 등록  2025-09-25 16:00 이렇게 보임
    public String getCreatedAtFormatted() {
        if (createdAt == null) return "";
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }



}
