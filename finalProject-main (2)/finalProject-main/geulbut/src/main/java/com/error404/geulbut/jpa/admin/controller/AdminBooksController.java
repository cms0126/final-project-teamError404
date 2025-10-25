package com.error404.geulbut.jpa.admin.controller;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.jpa.admin.service.AdminBooksService;
import com.error404.geulbut.jpa.authors.dto.AuthorsDto;
import com.error404.geulbut.jpa.books.dto.BooksDto;
import com.error404.geulbut.jpa.categories.dto.CategoriesDto;
import com.error404.geulbut.jpa.publishers.dto.PublishersDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/books")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBooksController {

    private final AdminBooksService adminBooksService;
    private final ErrorMsg errorMsg;

    // 도서 목록 페이지
    @GetMapping
    public String listBooksPage(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String keyword) {

        Page<BooksDto> dtoPage;
        if (keyword != null && !keyword.isEmpty()) {
            dtoPage = adminBooksService.searchBooks(keyword, page, size);
        } else {
            dtoPage = adminBooksService.getAllBooks(page, size);
        }

        model.addAttribute("booksPage", dtoPage);
        model.addAttribute("keyword", keyword); // JSP에서 input value로 유지
        return "admin/admin_books_list";
    }

    // 단일 도서 조회
    @GetMapping("/{bookId}")
    @ResponseBody
    public ResponseEntity<BooksDto> getBookById(@PathVariable Long bookId) {
        try {
            BooksDto bookDto = adminBooksService.getBookById(bookId);
            return ResponseEntity.ok(bookDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 관리자용 도서 상세 페이지 (JSP 반환)
    @GetMapping("/{bookId}/detail")
    public String bookDetailPage(@PathVariable Long bookId, Model model) {
        BooksDto book = adminBooksService.getBookById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("도서를 찾을 수 없습니다. bookId=" + bookId);
        }
        model.addAttribute("book", book);
        return "books/book_detail"; // <- 여기 폴더명 books로 수정
    }

    // 도서 등록
    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createBook(@RequestBody BooksDto bookDto) {
        try {
            BooksDto savedBook = adminBooksService.saveBook(bookDto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "book", savedBook
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 도서 수정
    @PutMapping("/{bookId}")
    @ResponseBody
    public ResponseEntity<BooksDto> updateBook(@PathVariable Long bookId,
                                               @RequestBody BooksDto bookDto) {
        bookDto.setBookId(bookId);
        try {
            BooksDto updatedBook = adminBooksService.updateBook(bookDto);
            return ResponseEntity.ok(updatedBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 도서 삭제
    @DeleteMapping("/{bookId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable Long bookId) {
        boolean deleted = adminBooksService.deleteBook(bookId);
        if (deleted) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", errorMsg.getMessage("admin.book.delete.success")
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", errorMsg.getMessage("admin.book.delete.notfound")
            ));
        }
    }

    // 검색
    @GetMapping("/search")
    @ResponseBody
    public Page<BooksDto> searchBooks(@RequestParam(required = false) String keyword,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return adminBooksService.searchBooks(keyword, page, size);
    }

    // 모달용 select 옵션 데이터 제공 (DTO 변환 적용)
    @GetMapping("/options")
    @ResponseBody
    public ResponseEntity<BookOptionsResponse> getBookOptions() {
        List<AuthorsDto> authors = adminBooksService.getAllAuthorsDto();
        List<PublishersDto> publishers = adminBooksService.getAllPublishersDto();
        List<CategoriesDto> categories = adminBooksService.getAllCategoriesDto();

        return ResponseEntity.ok(new BookOptionsResponse(authors, publishers, categories));
    }

    @Data
    @AllArgsConstructor
    static class BookOptionsResponse {
        private List<AuthorsDto> authors;
        private List<PublishersDto> publishers;
        private List<CategoriesDto> categories;
    }

    @GetMapping("/{bookId}/edit-options")
    @ResponseBody
    public ResponseEntity<BookEditOptionsResponse> getBookEditOptions(@PathVariable Long bookId) {
        Map<String, Object> data = adminBooksService.getBookAndRelations(bookId);

        BooksDto book = (BooksDto) data.get("book");
        List<AuthorsDto> authors = (List<AuthorsDto>) data.get("authors");
        List<PublishersDto> publishers = (List<PublishersDto>) data.get("publishers");
        List<CategoriesDto> categories = (List<CategoriesDto>) data.get("categories");

        return ResponseEntity.ok(new BookEditOptionsResponse(book, authors, publishers, categories));
    }

    @Data
    @AllArgsConstructor
    static class BookEditOptionsResponse {
        private BooksDto book;
        private List<AuthorsDto> authors;
        private List<PublishersDto> publishers;
        private List<CategoriesDto> categories;
    }

//    해시태그에 사용할 도서 전체 검색기능입니다.
    @GetMapping("/all")
    @ResponseBody
    public List<BooksDto> getAllBooksNoPage(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            // 검색 서비스 호출
            return adminBooksService.searchBooks(keyword, 0, 50) // 필요시 사이즈 조절
                    .getContent();
        } else {
            return adminBooksService.getAllBooksDto();
        }
    }
}