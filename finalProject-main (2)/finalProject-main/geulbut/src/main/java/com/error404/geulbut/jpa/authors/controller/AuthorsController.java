package com.error404.geulbut.jpa.authors.controller;


import com.error404.geulbut.jpa.authors.dto.AuthorsDto;
import com.error404.geulbut.jpa.authors.service.AuthorsService;
import com.error404.geulbut.jpa.books.dto.BooksDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class AuthorsController {

//  AuthorsService 가져오기
    private final AuthorsService authorsService;

    //	전체조회
    @GetMapping("/authors")
    public String selectAuthorsList(@PageableDefault(page = 0, size = 20) Pageable pageable,
                                  Model model) {

        Page<AuthorsDto> pages=authorsService.findAllAuthors(pageable);
        log.info("테스트 : "+pages);
        model.addAttribute("author", pages.getContent());
        model.addAttribute("page", pages);

        return "authors/authors_list";
    }

    @GetMapping("/authors/{authorId}")
    public String selectAuthorDetail(@PathVariable Long authorId, Model model) {
        AuthorsDto authorsDto = authorsService.findAuthorsById(authorId);
        List<BooksDto> booksDto = authorsService.findBooksByAuthorId(authorId);

        model.addAttribute("author", authorsDto);
        model.addAttribute("books", booksDto);

        return "authors/author_detail";
    }


}
