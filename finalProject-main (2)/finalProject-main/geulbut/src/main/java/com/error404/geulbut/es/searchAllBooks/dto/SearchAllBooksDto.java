package com.error404.geulbut.es.searchAllBooks.dto;

import lombok.*;
import java.util.List;

/**
 * ES 검색 결과 DTO
 * - JSP와 서비스에서 참조하는 필드 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SearchAllBooksDto {

    // 기본 정보
    private Long bookId;
    private String title;

    // 가격/재고
    private Long price;
    private Long discountedPrice;
    private Long stock;

    // 메타 정보
    private String authorName;
    private String categoryName;
    private String publisherName;
    private String bookImgUrl;
    private String isbn;
    private String nation;

    // 지표 (정렬/집계용)
    private Long salesCount;
    private Long wishCount;
    private Double popularityScore;

    // 날짜 (ISO8601 문자열 그대로 저장: 정렬은 ES에서 처리)
    private String pubDate;
    private String createdAt;
    private String updatedAt;

    // 태그
    private List<String> hashtags;

    // (선택) 하이라이트 결과
    private String titleHighlighted;
    private String authorNameHighlighted;
    private String publisherNameHighlighted;
    private String categoryNameHighlighted;
}
