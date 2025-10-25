package com.error404.geulbut.jpa.books.service;

import com.error404.geulbut.jpa.books.dto.BookApiDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookApiService {

    @Value("${api.key}")
    private String apiKey;
    @Value("${api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<BookApiDto> fetchBooks(String keyword,
                                       Integer pageNum,
                                       Integer pageSize,
                                       String systemType,
                                       String govYn) throws Exception {

        // 1) 쿼리 파라미터로 URL 구성 (자동 인코딩 처리됨)
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("key", apiKey)                                  // 발급키(필수)
                .queryParam("apiType", "json")                       // 응답 형식
                .queryParam("srchTarget", "total")                   // 검색 대상: 제목
                .queryParam("kwd", keyword)                                 // 검색어
                .queryParam("pageNum", pageNum == null ? 1 : pageNum)
                .queryParam("pageSize", pageSize == null ? 10 : pageSize);

        String url = builder.toUriString(); // 최종 URL
        // 3) API 호출 (원문 JSON)
        String json = restTemplate.getForObject(url, String.class);

        System.out.println("[BookApi] URL=" + url);
        System.out.println("[BookApi] raw json length=" + (json == null ? "null" : json.length()));
        // 4) JSON 파싱 → DTO 매핑 (필요한 필드만)
        //    ObjectMapper는 JSON 데이터를 자바 객체로 변환해 주는 도구
        //    mapper.readTree(json) : JSON 문자열을 트리(Tree) 구조로 변환(파일 시스템의 폴더처럼 계층적으로 탐색할 수 있게 )
        //    .path("docs"): 변환된 JSON 트리에서 **docs라는 이름의 노드 찾기
       List<BookApiDto> result = new ArrayList<>();
       if (json == null || json.isBlank()) return result;

       ObjectMapper mapper = new ObjectMapper();
       JsonNode root = mapper.readTree(json);

//       1) 배열 후보 경로들을 순서대로 탐색
        JsonNode array = null;
        String hitPath = null;
        String[] candidates = {
                "result",
                "result.docs",
                "docs",
                "items",
                "data",
                "response.docs"
        };

        for (String path : candidates) {
            JsonNode cur = root;
            for (String  p : path.split("\\.")) {
                cur = cur.path(p);
            }
            if (cur != null && cur.isArray() && cur.size() > 0) {
                array = cur;
                hitPath = path;
                break;
            }
        }
        System.out.println("[BookApi] array hit path=" + hitPath + ", size=" + (array == null ? 0 : array.size()));
        if (array == null) {
            System.out.println("[BookApi] root fieldNames=" + root.fieldNames().toString());
            return result;
        }
//        2) 필드명 변동 대응
        for (JsonNode item : array) {
            String title = pick(item, "titleInfo", "title", "bookname", "bookTitle");
            String author = pick(item, "authorInfo", "author", "authors", "writer");
            String publisher = pick(item, "pubInfo", "publisher", "pub", "publishing");
            String pubYear = pick(item, "pubYearInfo", "pubYear", "publication_year", "publishedDate");
            String imageUrl = pick(item, "imageUrl", "bookImageURL", "cover", "thumbnailUrl", "image");
            String description = pick(item, "description", "bookIntro", "overview", "contents", "desc");

            result.add(new BookApiDto(
                    nz(title), nz(author), nz(publisher), nz(pubYear), nz(imageUrl), nz(description)
            ));
    }
        System.out.println("[BookApi] mapped size=" + result.size());
        return result;
}
//        편의 유틸
private static String pick(JsonNode node, String... names) {
    for (String n : names) {
        JsonNode v = node.path(n);
        if (!v.isMissingNode() && !v.isNull()) {
        String s = v.asText();
        if (s != null && !s.isBlank())return s;
        }
    }
    return "";
    }
    private static String nz(String s) {return s == null ? "" : s;}
}
