package com.error404.geulbut.jpa.books.service;

import com.error404.geulbut.jpa.books.dto.BooksDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
@SpringBootTest
class BooksServiceTest {

    @Autowired
    BooksService booksService;
    

    @Test
    void findDetailByBookId() {
        long bookId = 317;
        BooksDto booksDto = booksService.findDetailByBookId(bookId);
        
        log.info("테스트 결과" + booksDto);
    }
}