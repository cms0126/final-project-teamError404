package com.error404.geulbut.jpa.bookhashtags.entity;

import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.hashtags.entity.Hashtags;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * BOOK_HASHTAGS 테이블과 매핑되는 JPA 엔티티
 * BOOK_ID + HASHTAG_ID를 복합키로 사용
 */
@Entity
@Table(name = "BOOK_HASHTAGS")
@IdClass(BookHashtagsId.class) // 복합키 클래스 지정
@Getter
@Setter
@NoArgsConstructor
public class BookHashtags {

    /** Books 테이블과 연관 (FK_BH_BOOKS) */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_ID", nullable = false)
    private Books book;

    /** Hashtags 테이블과 연관 (FK_BH_HASHTAGS) */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HASHTAG_ID", nullable = false)
    private Hashtags hashtag;

    /** 생성일 (CREATED_AT) */
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    /** 수정일 (UPDATED_AT) */
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    // 편의 생성자
    public BookHashtags(Books book, Hashtags hashtag) {
        this.book = book;
        this.hashtag = hashtag;
        this.createdAt = LocalDateTime.now();
    }
}
