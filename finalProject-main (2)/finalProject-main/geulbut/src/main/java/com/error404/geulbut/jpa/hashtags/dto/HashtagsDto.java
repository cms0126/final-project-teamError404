package com.error404.geulbut.jpa.hashtags.dto;

import com.error404.geulbut.jpa.books.dto.BooksDto;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "hashtagId")
public class HashtagsDto {
    private Long hashtagId;
    private String name;
    private LocalDateTime createdAt;

    // 해시태그에 연결된 책들
    private List<BooksDto> books = new ArrayList<>();

    // 날짜변환
    public String getCreatedAtFormatted() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}
