package com.error404.geulbut.jpa.books.controller;


import com.error404.geulbut.jpa.books.dto.BookApiDto;
import com.error404.geulbut.jpa.books.service.BookApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookViewController {

    private final BookApiService bookApiService;

//    홈 진입 시 이달의 주목도서 바인딩
    @GetMapping("/home")
    public String home(Model model) throws Exception {
        List<BookApiDto> featured = bookApiService.fetchBooks("이달의 주목도서", 1, 5, null, null);
        model.addAttribute("featuredBooks", featured);
        return "home";
    }
}
