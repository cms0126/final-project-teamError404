package com.error404.geulbut.jpa.admin.controller;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.jpa.admin.service.AdminAuthorsService;
import com.error404.geulbut.jpa.authors.dto.AuthorsDto;
import com.error404.geulbut.jpa.books.dto.BooksDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/authors")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuthorsController {

    private final AdminAuthorsService adminAuthorsService;
    private final ErrorMsg errorMsg;

    // 작가 목록 페이지 (검색 포함)
    @GetMapping
    public String listAuthorsPage(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String keyword) {

        Page<AuthorsDto> authorsDtoPage;

        if (keyword == null || keyword.isEmpty()) {
            authorsDtoPage = adminAuthorsService.getAllAuthors(page, size);
        } else {
            authorsDtoPage = adminAuthorsService.searchAuthors(keyword, page, size);
            model.addAttribute("keyword", keyword); // 검색어 유지
        }

        //  authorsPage 항상 모델에 추가 검색하고 나면 페이징처리가 풀리는걸 수정
        model.addAttribute("authorsPage", authorsDtoPage); // ✅ 항상 추가
        model.addAttribute("keyword", keyword); // 검색어 유지

        return "admin/admin_authors_list";
    }

    // 단일 작가 조회
    @GetMapping("/{authorId}")
    @ResponseBody
    public AuthorsDto getAuthorById(@PathVariable Long authorId) {
        return adminAuthorsService.getAuthorById(authorId);
    }

    // 작가 등록
    @PostMapping
    @ResponseBody
    public AuthorsDto createAuthor(@RequestBody AuthorsDto authorsDto) {
        return adminAuthorsService.saveAuthor(authorsDto);
    }

    // 작가 수정
    @PutMapping("/{authorId}")
    @ResponseBody
    public AuthorsDto updateAuthor(@PathVariable Long authorId,
                                   @RequestBody AuthorsDto authorsDto) {
        authorsDto.setAuthorId(authorId);
        return adminAuthorsService.updateAuthor(authorsDto);
    }

    // 작가 삭제
    @DeleteMapping("/{authorId}")
    @ResponseBody
    public boolean deleteAuthor(@PathVariable Long authorId) {
        return adminAuthorsService.deleteAuthor(authorId);
    }

//    작가별 책 목록 조회
    @GetMapping("/{authorId}/books")
    @ResponseBody
    public ResponseEntity<?> getBooksByAuthorId(@PathVariable Long authorId) {
        try {
            List<BooksDto> books = adminAuthorsService.getBooksByAuthor(authorId);
            return ResponseEntity.ok(books);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
