package com.error404.geulbut.es.searchAllBooks.service;

import com.error404.geulbut.es.searchAllBooks.dto.SearchAllBooksDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Log4j2
@SpringBootTest
class SearchAllBooksServiceTest {

    @Autowired
    SearchAllBooksService searchAllBooksService;

    @Test
    void searchByTemplate() throws Exception {

        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "노동";

        Page<SearchAllBooksDto> page = searchAllBooksService.searchByTemplate(keyword, pageable);

        log.info(page.getContent());
    }

}