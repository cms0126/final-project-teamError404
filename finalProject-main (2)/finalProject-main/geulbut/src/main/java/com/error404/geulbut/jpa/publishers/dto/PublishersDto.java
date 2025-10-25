package com.error404.geulbut.jpa.publishers.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "publisherId")
public class PublishersDto {
    private Long publisherId;
    private String name;
    private String description;
    private LocalDateTime createdAt;

//  날짜포맷 변환
    public String getCreatedAtFormatted() {
        if (createdAt == null) return "";
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}
