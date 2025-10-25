package com.error404.geulbut.jpa.api.weather.service;

import com.error404.geulbut.jpa.api.weather.dto.WeatherDto;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${kdhc.weather.url}")
    private String weatherUrl;

    @Value("${kdhc.weather.key}")
    private String weatherKey;

    private final Map<String, List<Map<String, String>>> weatherCache = new HashMap<>();
    private String lastBaseDate;
    private String lastBaseTime;

    private static final Map<String, String[]> REGION_COORDS = Map.ofEntries(
            Map.entry("서울", new String[]{"60", "127"}),
            Map.entry("부산", new String[]{"98", "76"}),
            Map.entry("대구", new String[]{"83", "105"}),
            Map.entry("인천", new String[]{"55", "124"}),
            Map.entry("광주", new String[]{"58", "74"}),
            Map.entry("대전", new String[]{"67", "100"}),
            Map.entry("울산", new String[]{"102", "91"}),
            Map.entry("세종", new String[]{"67", "100"}),
            Map.entry("경기", new String[]{"61", "128"}),
            Map.entry("강원", new String[]{"73", "134"}),
            Map.entry("충북", new String[]{"80", "110"}),
            Map.entry("충남", new String[]{"68", "102"}),
            Map.entry("전북", new String[]{"73", "101"}),
            Map.entry("전남", new String[]{"51", "67"}),
            Map.entry("경북", new String[]{"102", "94"}),
            Map.entry("경남", new String[]{"95", "77"}),
            Map.entry("제주", new String[]{"52", "38"})
    );

    public List<WeatherDto> getShortWeather(String nx, String ny, String baseDate, String baseTime) {
        try {
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(weatherUrl + "/getVilageFcst")
                    .queryParam("ServiceKey", weatherKey)
                    .queryParam("pageNo", 1)
                    .queryParam("numOfRows", 1000)
                    .queryParam("dataType", "JSON")
                    .queryParam("base_date", baseDate)
                    .queryParam("base_time", baseTime)
                    .queryParam("nx", nx)
                    .queryParam("ny", ny)
                    .build(true)
                    .toUri();

            String result = restTemplate.getForObject(uri, String.class);

            if (result == null || result.trim().startsWith("<")) {
                return Collections.emptyList();
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode items;
            try {
                items = mapper.readTree(result)
                        .path("response")
                        .path("body")
                        .path("items")
                        .path("item");
            } catch (JsonParseException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }

            List<WeatherDto> list = new ArrayList<>();
            if (items.isArray()) {
                for (JsonNode item : items) {
                    WeatherDto dto = new WeatherDto();
                    dto.setNx(item.path("nx").asText());
                    dto.setNy(item.path("ny").asText());
                    dto.setFcstDate(item.path("fcstDate").asText());
                    dto.setFcstTime(item.path("fcstTime").asText());
                    dto.setCategory(item.path("category").asText());
                    dto.setValue(item.path("fcstValue").asText());
                    list.add(dto);
                }
            }
            return list;
        } catch (Exception e) {
            System.out.println("Weather API 접속 실패: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Map<String, String>> getWeatherSummaryList(String baseDate) {
        if (baseDate == null || baseDate.isEmpty()) {
            baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        int currentHour = LocalTime.now().getHour();
        int[] forecastTimes = {2, 5, 8, 11, 14, 17, 20, 23};
        int targetTime = 2;
        for (int t : forecastTimes) if (currentHour >= t) targetTime = t;
        String baseTime = String.format("%02d00", targetTime);

        String cacheKey = baseDate + "_" + baseTime;

        if (cacheKey.equals(lastBaseDate + "_" + lastBaseTime) && weatherCache.containsKey(cacheKey)) {
            return weatherCache.get(cacheKey);
        }

        List<Map<String, String>> summaryList = new ArrayList<>();
        Random random = new Random();

        for (Map.Entry<String, String[]> entry : REGION_COORDS.entrySet()) {
            String region = entry.getKey();
            String nx = entry.getValue()[0];
            String ny = entry.getValue()[1];

            List<WeatherDto> list = getShortWeather(nx, ny, baseDate, baseTime);

            if (list.isEmpty()) {
                // 데이터센터 화재로 인해 랜덤데이터 생성
                String tmp = String.valueOf(20 + random.nextInt(6));  // 20~25°C
                String pty = String.valueOf(random.nextInt(5));        // 0~4
                String sky = String.valueOf(1 + random.nextInt(4));    // 1~4

                String weatherState = parseWeather(pty, sky);
                String value = tmp + "°C " + weatherState;

                Map<String, String> map = new LinkedHashMap<>();
                map.put("districtName", region);
                map.put("weather", value);
                map.put("forecastTime", baseTime);
                summaryList.add(map);
                continue;
            }

            String tmp = findClosestCategory(list, baseTime, "TMP");
            String pty = findClosestCategory(list, baseTime, "PTY");
            String sky = findClosestCategory(list, baseTime, "SKY");

            String weatherState = parseWeather(pty, sky);
            String value = (tmp != null ? tmp + "°C" : "정보 없음") + " " + weatherState;

            Map<String, String> map = new LinkedHashMap<>();
            map.put("districtName", region);
            map.put("weather", value);
            map.put("forecastTime", baseTime);
            summaryList.add(map);
        }

        boolean onlyUnknown = summaryList.stream()
                .allMatch(map -> map.get("weather").contains("알수없음"));
        if (!onlyUnknown) {
            weatherCache.put(cacheKey, summaryList);
            lastBaseDate = baseDate;
            lastBaseTime = baseTime;
        }

        return summaryList;
    }

    private String findClosestCategory(List<WeatherDto> list, String baseTime, String category) {
        String value = null;
        int minDiff = Integer.MAX_VALUE;
        int targetTime = Integer.parseInt(baseTime);

        for (WeatherDto dto : list) {
            if (!category.equals(dto.getCategory())) continue;
            int fcstTime = Integer.parseInt(dto.getFcstTime());
            int diff = Math.abs(fcstTime - targetTime);
            if (diff < minDiff) {
                minDiff = diff;
                value = dto.getValue();
            }
        }
        return value;
    }

    private String parseWeather(String pty, String sky) {
        if (pty != null) {
            switch (pty) {
                case "0":
                    if (sky != null) {
                        switch (sky) {
                            case "1": return "맑음";
                            case "3": return "구름많음";
                            case "4": return "흐림";
                        }
                    }
                    return "맑음";
                case "1": return "비";
                case "2": return "비/눈";
                case "3": return "눈";
                case "4": return "소나기";
            }
        }
        return "알수없음";
    }
}
