package com.error404.geulbut.jpa.bookhashtags.dto;

import com.error404.geulbut.jpa.books.dto.BooksDto;
import com.error404.geulbut.jpa.hashtags.dto.HashtagsDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookHashtagsDto {
    private BooksDto book;          // Books DTO
    private HashtagsDto hashtag;    // Hashtags DTO
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
