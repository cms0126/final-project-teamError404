package com.error404.geulbut.jpa.categories.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "categoryId")
public class CategoriesDto {
    private Long categoryId;
    private String name;
    private LocalDateTime createdAt;

//  출력 날짜 변환
    public String getCreatedAtFormatted() {
        if (createdAt == null) return "";
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
}
