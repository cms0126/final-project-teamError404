package com.error404.geulbut.es.searchAllBooks.controller;

import com.error404.geulbut.es.searchAllBooks.dto.SearchAllBooksDto;
import com.error404.geulbut.es.searchAllBooks.service.SearchAllBooksService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

/**
 * 통합검색 + 전체조회 컨트롤러 (ES 전용)
 * - q/keyword 모두 수용. 비어있으면 match_all.
 * - sort_field/sortField, sort_order/sortOrder 모두 수용.
 * - ★ 정렬 파라미터를 서비스로 반드시 전달(listAll(…, sf, so) / searchByTemplate(…, sf, so)).
 *
 * JSP 바인딩 키:
 *   pages, searches, keyword, pageNumber(1-base), totalPages, startPage, endPage, size,
 *   sortField, sortOrder
 */
@Log4j2
@Controller
@RequiredArgsConstructor
public class SearchAllBooksController {

    private final SearchAllBooksService searchAllBooksService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "popularity_score", "sales_count", "wish_count",
            "pub_date", "created_at", "updated_at", "price"
    );
    private static final Set<String> ALLOWED_SORT_ORDERS = Set.of("asc", "desc");

    @GetMapping("/search")
    public String search(
            // 검색어: q 우선 수용, 없으면 keyword 사용
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "keyword", required = false) String keyword,

            // 정렬 필드: snake/camel 모두 수용
            @RequestParam(value = "sort_field", required = false) String sortFieldSnake,
            @RequestParam(value = "sortField",   required = false) String sortFieldCamel,

            // 정렬 방향: snake/camel 모두 수용
            @RequestParam(value = "sort_order", required = false) String sortOrderSnake,
            @RequestParam(value = "sortOrder",   required = false) String sortOrderCamel,

            // 페이징
            @PageableDefault(page = 0, size = 10) Pageable pageable,

            Model model
    ) throws Exception {

        // 1) 검색어 정리 (q 우선 → keyword → 빈문자)
        final String kw = normalize(q, keyword);

        // 2) 정렬값 정리 (snake/camel 통합 + 화이트리스트)
        String sf = firstNonBlank(sortFieldSnake, sortFieldCamel, "popularity_score").trim().toLowerCase();
        if (!ALLOWED_SORT_FIELDS.contains(sf)) sf = "popularity_score";

        String so = firstNonBlank(sortOrderSnake, sortOrderCamel, "desc").trim().toLowerCase();
        if (!ALLOWED_SORT_ORDERS.contains(so)) so = "desc";

        log.info("[/search] q='{}' sort_field='{}' sort_order='{}' page={} size={}",
                kw, sf, so, pageable.getPageNumber(), pageable.getPageSize());

        // 3) ES 호출 — ★ 정렬값을 서비스에 '반드시' 전달
        Page<SearchAllBooksDto> pages =
                (kw == null || kw.isBlank())
                        ? searchAllBooksService.listAll(pageable, sf, so)               // match_all + 정렬
                        : searchAllBooksService.searchByTemplate(kw, pageable, sf, so); // 템플릿 + 정렬

        // 4) 페이징 계산 (JSP 1-base 규약)
        int pageZero   = pages.getNumber();
        int pageNumber = pageZero + 1;                    // 1-base
        int size       = pages.getSize();
        int totalPages = Math.max(pages.getTotalPages(), 1);

        int blockSize     = 10;
        int currentBlock  = (pageNumber - 1) / blockSize;
        int startPage     = currentBlock * blockSize + 1;
        int endPage       = Math.min(startPage + blockSize - 1, totalPages);

        // 5) 모델 바인딩
        model.addAttribute("pages", pages);
        model.addAttribute("searches", pages.getContent());
        model.addAttribute("keyword", kw);
        model.addAttribute("pageNumber", pageNumber);     // 1-base
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("size", size);
        model.addAttribute("sortField", sf);
        model.addAttribute("sortOrder", so);

        return "books/book_all";
    }

    /**
     * 선택사항: /books → /search 리다이렉트 (전체조회)
     */
    @GetMapping("/books")
    public String listRedirect(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        return "redirect:/search?q=&page=" + page + "&size=" + size;
    }

    /* ===================== 헬퍼 ===================== */

    private static String normalize(String first, String second) {
        String a = trimToEmpty(first);
        if (!a.isBlank()) return a;
        return trimToEmpty(second);
    }

    private static String firstNonBlank(String a, String b, String fallback) {
        String x = trimToEmpty(a);
        if (!x.isBlank()) return x;
        String y = trimToEmpty(b);
        return !y.isBlank() ? y : fallback;
    }

    private static String trimToEmpty(String s) {
        return s == null ? "" : s.trim();
    }
}
