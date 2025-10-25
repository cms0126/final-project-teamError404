package com.error404.geulbut.jpa.choice.service;

import com.error404.geulbut.jpa.choice.dto.ChoiceDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
@Log4j2
@SpringBootTest
class ChoiceServiceTest {

    @Autowired
    ChoiceService choiceService;

    @Test
    void getAllChoice() {
        Pageable pageable = PageRequest.of(0,4);
        Page<ChoiceDto> page =  choiceService.getAllChoice(pageable);
        log.info("테스트 : "+page.getContent());
    }
}