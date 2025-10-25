package com.error404.geulbut.jpa.admin.controller;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.jpa.admin.service.AdminHashtagService;
import com.error404.geulbut.jpa.books.dto.BooksDto;
import com.error404.geulbut.jpa.hashtags.dto.HashtagsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/hashtags")
@PreAuthorize("hasRole('ADMIN')")
public class AdminHashtagController {

    private final AdminHashtagService adminHashtagService;
    private final ErrorMsg errorMsg;

    // 해시태그 목록 페이지 (HTML 렌더링)
    @GetMapping
    public String listHashtagsPage(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String keyword) {

        Page<HashtagsDto> hashtagsPage;

        if (keyword == null || keyword.isEmpty()) {
            hashtagsPage = adminHashtagService.getAllHashtags(page, size);
        } else {
            hashtagsPage = adminHashtagService.searchHashtags(keyword, page, size);
            model.addAttribute("keyword", keyword);
        }

        model.addAttribute("hashtagsPage", hashtagsPage);
        return "admin/admin_hashtags_list"; // JSP 경로
    }

    // 단일 조회 (AJAX)
    @GetMapping("/{hashtagId}")
    @ResponseBody
    public HashtagsDto getHashtag(@PathVariable Long hashtagId) {
        return adminHashtagService.getHashtagById(hashtagId);
    }

    // 등록
    @PostMapping
    @ResponseBody
    public HashtagsDto createHashtag(@RequestBody HashtagsDto hashtagsDto) {
        return adminHashtagService.createHashtag(hashtagsDto);
    }

    // 수정
    @PutMapping("/{hashtagId}")
    @ResponseBody
    public HashtagsDto updateHashtag(@PathVariable Long hashtagId,
                                     @RequestBody HashtagsDto hashtagsDto) {
        return adminHashtagService.updateHashtag(hashtagId, hashtagsDto);
    }

    // 삭제
    @DeleteMapping("/{hashtagId}")
    @ResponseBody
    public boolean deleteHashtag(@PathVariable Long hashtagId) {
        return adminHashtagService.deleteHashtag(hashtagId);
    }

    // 해시태그별 도서 조회 (AJAX)
    @GetMapping("/{hashtagId}/books")
    @ResponseBody
    public List<BooksDto> getBooksByHashtag(@PathVariable Long hashtagId) {
        try {
            return adminHashtagService.getBooksByHashtag(hashtagId);
        } catch (Exception e) {
            throw new RuntimeException("도서를 불러오는 중 오류 발생", e);
        }
    }

    // 해시태그를 책에 연결 (AJAX)
    @PostMapping("/{hashtagId}/books/{bookId}")
    @ResponseBody
    public void addBookToHashtag(@PathVariable Long hashtagId, @PathVariable Long bookId) {
        adminHashtagService.addHashtagToBook(bookId, hashtagId);
    }

    // 책에서 해시태그 연결 제거 (AJAX)
    @DeleteMapping("/{hashtagId}/books/{bookId}")
    @ResponseBody
    public void removeBookFromHashtag(@PathVariable Long hashtagId, @PathVariable Long bookId) {
        adminHashtagService.removeHashtagFromBook(bookId, hashtagId);
    }
}
