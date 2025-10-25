package com.error404.geulbut.jpa.authors.service;

import com.error404.geulbut.jpa.authors.dto.AuthorsDto;
import com.error404.geulbut.jpa.books.dto.BooksDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;


@Log4j2
@SpringBootTest
class AuthorsServiceTest {
    @Autowired
    private AuthorsService authorsService;

    @Test
    void findAllAuthors() {
        Pageable pageable = PageRequest.of(0,3);
        Page<AuthorsDto> page = authorsService.findAllAuthors(pageable);
        log.info("테스트" + page.getContent());
    }

    @Test
    void findBooksByAuthorId() {
        Long authorId = 302L;
        List<BooksDto> booksDto = authorsService.findBooksByAuthorId(authorId);
        log.info("===조회테스트===");
        booksDto.forEach(book-> log.info(book));
    }
}