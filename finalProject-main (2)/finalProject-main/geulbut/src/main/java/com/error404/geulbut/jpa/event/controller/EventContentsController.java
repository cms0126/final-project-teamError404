package com.error404.geulbut.jpa.event.controller;

import com.error404.geulbut.jpa.event.entity.EventContents;
import com.error404.geulbut.jpa.event.service.EventContentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequiredArgsConstructor
public class EventContentsController {
    //	서비스 가져오기
    private final EventContentsService eventContentsService;

    //	전체조회
    @GetMapping("/recommended")
    public String selectDeptList( @RequestParam(defaultValue = "") String searchKeyword,
                                  @PageableDefault(page = 0, size = 10) Pageable pageable,
                                  Model model) {
//		1) Pageable : page(현재페이지), size(1페이지 당 화면에 보일개수)
//		Pageable pageable = PageRequest.of(page, size);
//		전체조회 서비스 메소드 실행
        Page<EventContents> pagesA=eventContentsService.selectEventContentsListA(pageable);
        model.addAttribute("eventcontentsA", pagesA.getContent());
        Page<EventContents> pagesB=eventContentsService.selectEventContentsListB(pageable);
        model.addAttribute("eventcontentsB", pagesB.getContent());
        return "recommended/recommended_all";
    }
}
