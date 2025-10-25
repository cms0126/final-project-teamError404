package com.error404.geulbut.jpa.api.weather.controller;

import com.error404.geulbut.jpa.api.weather.service.WeatherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * /testWeather
     * 뉴스형 날씨 출력 페이지
     */
    @GetMapping("/weather")
    public String weatherNews(Model model) throws JsonProcessingException {
        List<Map<String, String>> summaryList = weatherService.getWeatherSummaryList(null);

        // ObjectMapper로 JSON 문자열 생성
        ObjectMapper mapper = new ObjectMapper();
        String mergedJson = mapper.writeValueAsString(summaryList);

        model.addAttribute("mergedJson", mergedJson); // JSP에서 ${mergedJson} 사용
        return "api/DustWeatherApi";
    }
}
