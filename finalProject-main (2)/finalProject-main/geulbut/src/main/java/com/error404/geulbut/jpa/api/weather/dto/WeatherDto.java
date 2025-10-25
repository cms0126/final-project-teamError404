package com.error404.geulbut.jpa.api.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WeatherDto {
    private String nx;       // X 좌표
    private String ny;       // Y 좌표
    private String fcstDate; // 예보 날짜 (yyyyMMdd)
    private String fcstTime; // 예보 시간 (HHmm)
    private String category; // 예보 항목 (TMP, TMX, TMN, POP 등)
    private String value;    // 예보 값

}
