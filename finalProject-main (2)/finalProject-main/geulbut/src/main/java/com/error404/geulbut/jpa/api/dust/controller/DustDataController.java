package com.error404.geulbut.jpa.api.dust.controller;

import com.error404.geulbut.jpa.api.dust.service.DustService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


// 미세먼지 데이터를 JSON으로 반환하는 역할 - 강대성
@RequiredArgsConstructor
@RestController
public class DustDataController {

    private final DustService dustService;

    @GetMapping("/dustApi")
    public Map<String, String> getDustSimple() {

        return dustService.getMajorSidoDustSimple();
    }
}
