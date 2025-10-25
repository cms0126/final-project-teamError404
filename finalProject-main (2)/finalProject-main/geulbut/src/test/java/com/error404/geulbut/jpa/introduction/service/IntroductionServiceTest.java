package com.error404.geulbut.jpa.introduction.service;

import com.error404.geulbut.jpa.introduction.dto.IntroductionDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Log4j2
@SpringBootTest
class IntroductionServiceTest {

    @Autowired
    IntroductionService introductionService;

    @Test
    void selectDeptList() {

    }

    @Test
    void getAllIntroductions() {
    }
}