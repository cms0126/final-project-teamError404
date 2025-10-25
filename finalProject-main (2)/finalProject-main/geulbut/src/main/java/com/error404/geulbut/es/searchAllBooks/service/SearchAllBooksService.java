package com.error404.geulbut.es.searchAllBooks.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.SearchTemplateRequest;
import co.elastic.clients.elasticsearch.core.SearchTemplateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.es.searchAllBooks.dto.SearchAllBooksDto;
import com.error404.geulbut.es.searchAllBooks.entity.SearchAllBooks;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class SearchAllBooksService {

    private static final String INDEX = "search-all-books";
    private static final String TEMPLATE_ID = "book_unified_search";

    /** ✅ JSP/템플릿에서 쓰는 정렬 필드 전체 허용 */
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "popularity_score",
            "sales_count",
            "wish_count",
            "pub_date",
            "created_at",
            "updated_at",
            "price"
    );
    private static final Set<String> ALLOWED_SORT_ORDERS = Set.of("asc", "desc");

    private final ElasticsearchClient client;
    private final MapStruct mapStruct;

    /* ------------------------------ 공통 가드 ------------------------------ */

    private String normSortField(String sf) {
        if (sf == null) return "popularity_score";
        sf = sf.trim().toLowerCase();
        return ALLOWED_SORT_FIELDS.contains(sf) ? sf : "popularity_score";
    }

    private String normSortOrder(String so) {
        if (so == null) return "desc";
        so = so.trim().toLowerCase();
        return ALLOWED_SORT_ORDERS.contains(so) ? so : "desc";
    }

    private SortOrder toSortOrder(String so) {
        return "asc".equalsIgnoreCase(so) ? SortOrder.Asc : SortOrder.Desc;
    }

    /* ------------------------------ 변환 유틸 (하이라이트 적용 포함) ------------------------------ */

    private List<SearchAllBooksDto> toDtoList(SearchResponse<SearchAllBooks> res) {
        return res.hits().hits().stream()
                .map(hit -> toDtoWithHighlight(hit.source(), hit.highlight()))
                .filter(Objects::nonNull)
                .toList();
    }

    private List<SearchAllBooksDto> toDtoList(SearchTemplateResponse<SearchAllBooks> res) {
        return res.hits().hits().stream()
                .map(hit -> toDtoWithHighlight(hit.source(), hit.highlight()))
                .filter(Objects::nonNull)
                .toList();
    }

    private SearchAllBooksDto toDtoWithHighlight(SearchAllBooks src, Map<String, List<String>> highlight) {
        if (src == null) return null;

        SearchAllBooksDto dto = mapStruct.toDto(src);

        // 기본 보정
        try {
            Double ps = dto.getPopularityScore();
            if (ps == null || !Double.isFinite(ps)) dto.setPopularityScore(0.0);
        } catch (Exception ignore) {
            dto.setPopularityScore(0.0);
        }
        if (dto.getHashtags() == null) dto.setHashtags(Collections.emptyList());

        // 하이라이트 적용 (없으면 원문 유지)
        dto.setTitleHighlighted(firstOrFallback(highlight, "title", dto.getTitle()));
        dto.setAuthorNameHighlighted(firstOrFallback(highlight, "author_name", dto.getAuthorName()));
        dto.setPublisherNameHighlighted(firstOrFallback(highlight, "publisher_name", dto.getPublisherName()));
        dto.setCategoryNameHighlighted(firstOrFallback(highlight, "category_name", dto.getCategoryName()));

        return dto;
    }

    private String firstOrFallback(Map<String, List<String>> hl, String key, String fallback) {
        if (hl == null || hl.isEmpty()) return fallback;
        List<String> vals = hl.get(key);
        if (vals == null || vals.isEmpty()) return fallback;
        String v = vals.get(0);
        return (v == null || v.isBlank()) ? fallback : v;
    }

    /* ------------------------------ match_all (키워드 빈 문자열일 때) ------------------------------ */

    /** 기존 시그니처 유지 — 기본 정렬: popularity_score desc */
    public Page<SearchAllBooksDto> listAll(Pageable pageable) throws Exception {
        return listAll(pageable, "popularity_score", "desc");
    }

    /** 오버로드 — match_all + 동적 정렬(프런트 sortField/sortOrder 반영) */
    public Page<SearchAllBooksDto> listAll(Pageable pageable, String sortField, String sortOrder) throws Exception {
        final int size = pageable.getPageSize();
        final int from = (int) pageable.getOffset();

        final String sf = normSortField(sortField);
        final String so = normSortOrder(sortOrder);

        log.debug("[ES] match_all sort={} order={} from={} size={}", sf, so, from, size);

        SearchRequest req = SearchRequest.of(b -> b
                .index(INDEX)
                .from(from)
                .size(size)
                .trackTotalHits(t -> t.enabled(true))
                .query(q -> q.matchAll(m -> m))
                // 1차: 선택 정렬, 2차: _score desc, 3차: created_at desc (fallback)
                .sort(s -> s.field(f -> f.field(sf).order(toSortOrder(so)).missing("_last")))
                .sort(s -> s.score(sc -> sc.order(SortOrder.Desc)))
                .sort(s -> s.field(f -> f.field("created_at").order(SortOrder.Desc).missing("_last")))
        );

        SearchResponse<SearchAllBooks> res = client.search(req, SearchAllBooks.class);

        List<SearchAllBooksDto> content = toDtoList(res);
        long total = (res.hits().total() != null) ? res.hits().total().value() : content.size();

        return new PageImpl<>(content, pageable, total);
    }

    /** 편의 오버로드 — 컨트롤러가 (sf, so, pageable) 순서로 호출해도 수용 */
    public Page<SearchAllBooksDto> listAll(String sortField, String sortOrder, Pageable pageable) throws Exception {
        return listAll(pageable, sortField, sortOrder);
    }

    /* ------------------------------ 템플릿 검색 (키워드 있을 때) ------------------------------ */

    /** 기존 시그니처 유지 — 기본 정렬: popularity_score desc */
    public Page<SearchAllBooksDto> searchByTemplate(String keyword, Pageable pageable) throws Exception {
        return searchByTemplate(keyword, pageable, "popularity_score", "desc");
    }

    /** 템플릿 호출 — q/size/from + sort_field/sort_order 전달 (하이라이트 포함 응답을 DTO에 반영) */
    public Page<SearchAllBooksDto> searchByTemplate(String keyword, Pageable pageable,
                                                    String sortField, String sortOrder) throws Exception {
        final String q = (keyword == null) ? "" : keyword;
        final int size = pageable.getPageSize();
        final int from = (int) pageable.getOffset();

        final String sf = normSortField(sortField);
        final String so = normSortOrder(sortOrder);

        Map<String, JsonData> params = new HashMap<>();
        params.put("q",           JsonData.of(q));
        params.put("size",        JsonData.of(size));
        params.put("from",        JsonData.of(from));
        params.put("sort_field",  JsonData.of(sf));   // ← 템플릿에서 사용 (조건 블록)
        params.put("sort_order",  JsonData.of(so));   // ← 템플릿에서 사용 (조건 블록)

        log.debug("[ES] template={} q='{}' sort={} order={} from={} size={}",
                TEMPLATE_ID, q, sf, so, from, size);

        SearchTemplateRequest req = SearchTemplateRequest.of(b -> b
                .index(INDEX)
                .id(TEMPLATE_ID)
                .params(params)
        );

        SearchTemplateResponse<SearchAllBooks> res =
                client.searchTemplate(req, SearchAllBooks.class);

        List<SearchAllBooksDto> content = toDtoList(res);
        long total = (res.hits().total() != null) ? res.hits().total().value() : content.size();

        return new PageImpl<>(content, pageable, total);
    }
}
