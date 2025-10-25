package com.error404.geulbut.jpa.bookhashtags.service;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.bookhashtags.dto.BookHashtagsDto;
import com.error404.geulbut.jpa.bookhashtags.entity.BookHashtags;
import com.error404.geulbut.jpa.bookhashtags.entity.BookHashtagsId;
import com.error404.geulbut.jpa.bookhashtags.repository.BookHashtagsRepository;
import com.error404.geulbut.jpa.books.repository.BooksRepository;
import com.error404.geulbut.jpa.hashtags.repository.HashtagsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookHashtagsService {

    private final BookHashtagsRepository bookHashtagsRepository;
    private final MapStruct mapStruct;

    // 특정 책에 연결된 해시태그 조회
    public List<BookHashtagsDto> getByBookId(Long bookId) {
        return bookHashtagsRepository.findByBook_BookId(bookId)
                .stream()
                .map(mapStruct::toDto)
                .collect(Collectors.toList());
    }

    // 특정 해시태그가 연결된 책 조회
    public List<BookHashtagsDto> getByHashtagId(Long hashtagId) {
        return bookHashtagsRepository.findByHashtag_HashtagId(hashtagId)
                .stream()
                .map(mapStruct::toDto)
                .collect(Collectors.toList());
    }

    //    책 제목으로 해시태그 조회
    public List<BookHashtagsDto> getByBookTitle(String bookTitle) {
        return bookHashtagsRepository.findByBook_TitleContaining(bookTitle)
                .stream()
                .map(mapStruct::toDto)
                .collect(Collectors.toList());
    }

//    해시태그 이름으로 책 조회
    public List<BookHashtagsDto> getByHashtagName(String hashtagName) {
        return bookHashtagsRepository.findByHashtag_NameContaining(hashtagName)
                .stream()
                .map(mapStruct::toDto)
                .collect(Collectors.toList());
    }
}
