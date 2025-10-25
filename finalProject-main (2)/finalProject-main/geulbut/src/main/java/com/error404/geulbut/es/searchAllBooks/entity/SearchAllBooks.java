package com.error404.geulbut.es.searchAllBooks.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "search-all-books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true) // ES _source에 추가 필드가 있어도 무시
public class SearchAllBooks {

    /* 기본 정보 */
    @Id
    @JsonProperty("book_id")
    private Long bookId;

    private String title;

    /* 가격/재고 */
    private Long price;

    @JsonProperty("discounted_price")
    private Long discountedPrice;

    private Long stock;

    /* 메타 정보 */
    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("publisher_name")
    private String publisherName;

    @JsonProperty("book_img_url")
    private String bookImgUrl;

    private String isbn;       // ES: keyword
    private String nation;     // ES: keyword (null 가능)

    /* 지표(정렬/집계용) */
    @JsonProperty("sales_count")
    private Long salesCount;

    @JsonProperty("wish_count")
    private Long wishCount;

    @JsonProperty("popularity_score")
    private Double popularityScore;

    /* 날짜 (ES는 ISO8601 문자열로 오므로 String으로 수용; 정렬은 ES에서 처리) */
    @JsonProperty("pub_date")
    private String pubDate;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    /* 해시태그 */
    private List<String> hashtags;
}
