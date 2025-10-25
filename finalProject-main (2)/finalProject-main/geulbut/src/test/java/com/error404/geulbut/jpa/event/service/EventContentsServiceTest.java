package com.error404.geulbut.jpa.event.service;

import com.error404.geulbut.jpa.event.entity.EventContents;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class EventContentsServiceTest {
    @Autowired
    EventContentsService eventContentsService;
    @Test
    void selectEventContentsList() {
    }

    @Test
    void testSelectEventContentsList() {
    }

    @Test
    void testSelectEventContentsList1() {
    }

    @Test
    void selectEventContentsListA() {
        String searchKeyword="";
        Pageable pageable = PageRequest.of(0,10);
        Page<EventContents> page = eventContentsService.selectEventContentsListA(pageable);
        log.info("테스트 : "+page.getContent());  // 패이지 안에 content 에 dept 객체가 있습니다.
    }

    @Test
    void selectEventContentsListB() {
    }
}