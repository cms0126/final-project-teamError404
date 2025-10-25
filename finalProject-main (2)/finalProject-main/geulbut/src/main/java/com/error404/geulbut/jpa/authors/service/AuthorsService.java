package com.error404.geulbut.jpa.authors.service;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.authors.dto.AuthorsDto;
import com.error404.geulbut.jpa.authors.entity.Authors;
import com.error404.geulbut.jpa.authors.repository.AuthorsRepository;
import com.error404.geulbut.jpa.books.dto.BooksDto;
import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.books.repository.BooksRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorsService {
    private final AuthorsRepository authorsRepository;
    private final BooksRepository booksRepository;
    private final MapStruct mapStruct;
    private final ErrorMsg errorMsg;

    public Page<AuthorsDto> findAllAuthors(Pageable pageable) {
        Page<Authors> page = authorsRepository.findAll(pageable);
        return page.map(authors ->  mapStruct.toDto(authors));
    }

    public AuthorsDto findAuthorsById(Long authorId) {
        Authors authors = authorsRepository.findById(authorId).orElse(null);
        return mapStruct.toDto(authors);

    }

    public List<BooksDto> findBooksByAuthorId(Long authorId) {
        List<Books> books = booksRepository.findByAuthor_AuthorId(authorId);
        return books.stream()
                .map(mapStruct::toDto)
                .toList();
    }



}
