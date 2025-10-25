package com.error404.geulbut.jpa.books.controller;

import com.error404.geulbut.jpa.books.dto.BookApiDto;
import com.error404.geulbut.jpa.books.service.BookApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
// RESTful API 구조를 위한 @RequestMapping("/api/books") 경로 지정
@RequestMapping("/api/books")
public class BookApiController {
    private final BookApiService bookApiService;

    @GetMapping ("/listBooks")  // /api/books/listBooks
    public List<BookApiDto> listBooks(@RequestParam String keyword ) throws  Exception {
        return bookApiService.fetchBooks(keyword, 1, 10, null, null);
    }
}
