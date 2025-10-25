package com.error404.geulbut.jpa.api.weather.controller;

import com.error404.geulbut.jpa.api.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class WeatherDataController {

    // 날씨 데이터를 JSON으로 반환하는 역할 - 강대성

    private final WeatherService weatherService;
    /**
     * 전국 주요 10개 지역 날씨 요약
     */
    @GetMapping("/weatherApi")
    public List<Map<String, String>> getWeatherSummary() {
        return weatherService.getWeatherSummaryList(null); // 오늘 기준
    }
}
