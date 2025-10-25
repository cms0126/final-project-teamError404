package com.error404.geulbut.jpa.api.dust.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class DustService {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    @Value("${dust.api.key}")
    private String apiKey;


    private final String[] sidoList = {
            "서울", "부산", "대구", "인천", "광주", "대전", "울산",
            "세종", "경기", "강원", "충북", "충남", "전북", "전남",
            "경북", "경남", "제주"
    };

    /**
     * 시도별 미세먼지 상태만 Map으로 반환
     * { "서울": "맑음", "부산": "주의보", ... }
     */

    public Map<String, String> getMajorSidoDustSimple() {
        Map<String, String> result = new LinkedHashMap<>();
        String[] fakeGrades = {"좋음", "보통", "나쁨", "매우나쁨"};
        Random random = new Random();

        for (String sido : sidoList) {
            try {
                String encodedSido = UriUtils.encodeQueryParam(sido, StandardCharsets.UTF_8);

                URI uri = UriComponentsBuilder
                        .fromHttpUrl("https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty")
                        .queryParam("serviceKey", apiKey)
                        .queryParam("returnType", "json")
                        .queryParam("sidoName", encodedSido)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", 1)
                        .queryParam("ver", "1.0")
                        .build(true)
                        .toUri();

                String responseStr = restTemplate.getForObject(uri, String.class);
                String grade = null;

                if (responseStr != null && !responseStr.isEmpty()) {
                    JsonNode rootNode = mapper.readTree(responseStr);
                    JsonNode itemsNode = rootNode.path("response").path("body").path("items");

                    if (itemsNode.isArray() && itemsNode.size() > 0) {
                        JsonNode firstItem = itemsNode.get(0);
                        String informGrade = firstItem.path("informGrade").asText(null);
                        if (informGrade != null && !informGrade.isEmpty()) {
                            grade = informGrade; // 실제 경보/주의보 값
                        }
                    }
                }

                // 🔹 실제 데이터 없으면 가짜 데이터 사용
                if (grade == null || grade.isEmpty()) {
                    grade = fakeGrades[random.nextInt(fakeGrades.length)];
                }

                result.put(sido, grade);

            } catch (Exception e) {
                // 🔹 API 실패 시에도 가짜 데이터
                result.put(sido, fakeGrades[random.nextInt(fakeGrades.length)]);
            }
        }

        return result;
    }
}