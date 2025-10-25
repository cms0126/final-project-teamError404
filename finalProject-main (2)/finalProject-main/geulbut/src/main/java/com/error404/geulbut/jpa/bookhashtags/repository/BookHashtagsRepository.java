package com.error404.geulbut.jpa.bookhashtags.repository;

import com.error404.geulbut.jpa.bookhashtags.entity.BookHashtags;
import com.error404.geulbut.jpa.bookhashtags.entity.BookHashtagsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookHashtagsRepository extends JpaRepository<BookHashtags, BookHashtagsId> {

    // 특정 책에 연결된 모든 해시태그 조회
    List<BookHashtags> findByBook_BookId(Long bookId);

    // 특정 해시태그에 연결된 모든 책 조회
    List<BookHashtags> findByHashtag_HashtagId(Long hashtagId);

    // 책 제목 일부 일치 검색
    List<BookHashtags> findByBook_TitleContaining(String title);

    // 해시태그 이름 일부 일치 검색
    List<BookHashtags> findByHashtag_NameContaining(String name);

    // 특정 책과 특정 해시태그 연결만 삭제
    void deleteByBook_BookIdAndHashtag_HashtagId(Long bookId, Long hashtagId);

    // 특정 해시태그 삭제 시, 연결된 모든 BookHashtags 레코드 삭제
    void deleteByHashtag_HashtagId(Long hashtagId);

    // 특정 책 삭제 시, 연결된 모든 BookHashtags 레코드 삭제
    void deleteByBook_BookId(Long bookId);

    // 특정 해시태그가 어떤 책과 연결되어 있는지 존재 여부 확인
    boolean existsByHashtag_HashtagId(Long hashtagId);

    // 해시태그별 모든 책 조회
    List<BookHashtags> findAllByHashtag_HashtagId(Long hashtagId);
}
