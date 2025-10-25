package com.error404.geulbut.jpa.books.controller;

import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.authors.service.AuthorsService;
import com.error404.geulbut.jpa.books.dto.BooksDto;
import com.error404.geulbut.jpa.books.repository.BooksRepository;
import com.error404.geulbut.jpa.books.service.BooksService;
import com.error404.geulbut.jpa.reviews.service.ReviewsQueryService;
import com.error404.geulbut.jpa.users.entity.Users;
import com.error404.geulbut.jpa.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.Map;


@Log4j2
@Controller
@RequiredArgsConstructor
public class BooksController {

    private final BooksService booksService;
    private final UsersService usersService;
    private final BooksRepository booksRepository;
    private final MapStruct mapStruct;
    private final AuthorsService authorsService;
    private final ReviewsQueryService reviewsQueryService;


    // application.yml(or properties)에 정의된 값. 없으면 imp_test로 기본(개발 임시) 세팅
    @Value("${portone.imp_code}")
    private String iamportCode;

    @GetMapping("/book/{bookId}")
    public String bookDetail(@PathVariable long bookId,
                             Model model,
                             Authentication authentication) {

        BooksDto book = booksService.findDetailByBookId(bookId);

        // ✅ 할인율 계산 추가
        if (book.getPrice() != null && book.getDiscountedPrice() != null && book.getPrice() > 0) {
            double rate = ((book.getPrice() - book.getDiscountedPrice()) * 100.0) / book.getPrice();
            book.setDiscountRate((double) Math.round(rate)); // 반올림된 % 값
        } else {
            book.setDiscountRate(0.0);
        }
        model.addAttribute("book", book);

        // 저자 정보 + 같은 작가의 다른 도서 (있을때만)
        if (book.getAuthorId() != null) {
            var author = authorsService.findAuthorsById(book.getAuthorId());
            model.addAttribute("author", author);

            // ✅ 가변 리스트로 복사해서 조작
            List<BooksDto> moreByAuthor = new ArrayList<>(
                    Optional.ofNullable(authorsService.findBooksByAuthorId(book.getAuthorId()))
                            .orElseGet(List::of) // null 방지
            );

            // 현재 도서 제외
            moreByAuthor.removeIf(b -> Objects.equals(b.getBookId(), bookId));

            // 최대 4권만 (subList도 다시 복사해서 불변/뷰 리스트 이슈 방지)
            if (moreByAuthor.size() > 4) {
                moreByAuthor = new ArrayList<>(moreByAuthor.subList(0, 4));
            }

            model.addAttribute("moreByAuthor", moreByAuthor);
        }


        // 로그인 사용자 내려주기 (모달에 user.* 바인딩)
        if (authentication != null && authentication.isAuthenticated()) {
            String userId = authentication.getName();
            Users user = usersService.getUserById(userId);   // UsersService에 이미 존재하는 조회 메서드

            Map<String,Object> userinfo = Map.of(
                    "userId", user.getUserId(),
                    "userName", user.getName(),   // 여기서 JSP용으로 키 맞춰줌
                    "email", user.getEmail(),
                    "phone", user.getPhone(),
                    "address", user.getAddress()
            );
            model.addAttribute("user", userinfo);
        }

        // 아임포트 가맹점 코드(JS 초기화용)
        model.addAttribute("iamportCode", iamportCode);

        var rv = reviewsQueryService.getSummary(bookId);
        var recent = reviewsQueryService.getRecentShortReviews(bookId, 3);

        model.addAttribute("rv", rv);
        model.addAttribute("reviews", recent);

        return "books/book_detail";
    }

    @GetMapping("/books/search")
    public String searchBooksByAuthor(String q, Model model) {
        List<BooksDto> books;

        if (q != null && !q.isEmpty()) {
            // 작가 이름으로 책 검색
            books = booksRepository.findByAuthorNameContaining(q)
                    .stream()
                    .map(mapStruct::toDto)
                    .toList();
        } else {
            books = List.of(); // 빈 리스트
        }

        model.addAttribute("books", books);
        model.addAttribute("keyword", q); // 검색창에 표시
        return "authors/authors_search"; // JSP
    }
}
