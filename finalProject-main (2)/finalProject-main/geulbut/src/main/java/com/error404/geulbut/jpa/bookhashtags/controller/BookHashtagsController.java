package com.error404.geulbut.jpa.bookhashtags.controller;

import com.error404.geulbut.jpa.bookhashtags.service.BookHashtagsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequiredArgsConstructor
public class BookHashtagsController {

    private final BookHashtagsService bookHashtagsService;

    // 기존 조회 기능 (ID 기준)
    @GetMapping("/book-hashtags")
    public String viewAll(Model model,
                          @RequestParam(required = false) Long bookId,
                          @RequestParam(required = false) Long hashtagId) {

//        책 기준 해시태그 검색
        if (bookId != null) {
            model.addAttribute("bookHashtagsByBook", bookHashtagsService.getByBookId(bookId));
            model.addAttribute("bookId", bookId);
        }
//        해시태그 기준 책 검색
        if (hashtagId != null) {
            model.addAttribute("bookHashtagsByHashtag", bookHashtagsService.getByHashtagId(hashtagId));
            model.addAttribute("hashtagId", hashtagId);
        }

        return "bookhashtags/book_hashtags_all";
    }

    // 통합 검색: 책/해시태그 각각 처리
    @GetMapping("/book-hashtags/search")
    public String search(
            @RequestParam(required = false) String bookQuery,
            @RequestParam(required = false) String hashtagQuery,
            Model model) {

        // 책 검색 처리
        if (bookQuery != null && !bookQuery.isBlank()) {
            if (bookQuery.matches("\\d+")) {
                Long bookId = Long.parseLong(bookQuery);
                model.addAttribute("bookSearchResult", bookHashtagsService.getByBookId(bookId));
            } else {
                model.addAttribute("bookSearchResult", bookHashtagsService.getByBookTitle(bookQuery));
            }
            model.addAttribute("queryBook", bookQuery);
        }

        // 해시태그 검색 처리
        if (hashtagQuery != null && !hashtagQuery.isBlank()) {
            if (hashtagQuery.matches("\\d+")) {
                Long hashtagId = Long.parseLong(hashtagQuery);
                model.addAttribute("hashtagSearchResult", bookHashtagsService.getByHashtagId(hashtagId));
            } else {
                model.addAttribute("hashtagSearchResult", bookHashtagsService.getByHashtagName(hashtagQuery));
            }
            model.addAttribute("queryHashtag", hashtagQuery);
        }

        return "bookhashtags/book_hashtags_all";
    }
}
