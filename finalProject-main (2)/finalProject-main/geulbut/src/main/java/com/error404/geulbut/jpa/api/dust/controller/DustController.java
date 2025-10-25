package com.error404.geulbut.jpa.api.dust.controller;


import com.error404.geulbut.jpa.api.dust.service.DustService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DustController {

    private final DustService dustService;

    @GetMapping("/dust")
    public String testDust(Model model) {

        Map<String, String> dustMap = dustService.getMajorSidoDustSimple();

        if (dustMap == null || dustMap.isEmpty()) {
            model.addAttribute("dustData", "데이터가 없습니다.");
        } else {
            model.addAttribute("dustData", dustMap);
        }

        return "WeatherDustApi"; // /WEB-INF/views/testDust.jsp
    }
}
