package com.error404.geulbut.jpa.bookhashtags.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * JPA에서 복합키(Composite Key)로 사용하는 ID 클래스
 * BOOK_HASHTAGS 테이블의 PK는 BOOK_ID + HASHTAG_ID로 구성되어 있으므로,
 * 이 두 필드를 기준으로 equals()와 hashCode()를 정의해야 한다.
 */
public class BookHashtagsId implements Serializable { // Serializable 필수, JPA에서 복합키를 직렬화해야 함

    private Long book;    // Books 테이블의 PK (BOOK_ID)
    private Long hashtag; // Hashtags 테이블의 PK (HASHTAG_ID)

    // 기본 생성자: JPA가 내부적으로 객체를 생성할 때 필요
    public BookHashtagsId() {}

    // 편의 생성자: 두 필드를 동시에 초기화할 때 사용
    public BookHashtagsId(Long book, Long hashtag) {
        this.book = book;
        this.hashtag = hashtag;
    }

    /**
     * equals() 재정의
     * 두 객체가 같은 복합키(book + hashtag)를 가지면 같은 객체로 간주
     * JPA가 PK 비교 및 Set, Map 등에서 올바르게 작동하게 하기 위해 필요
     */
    @Override
    public boolean equals(Object o) {
        // 자기 자신 비교 시 바로 true 반환 (성능 최적화)
        if (this == o) return true;

        // null이거나 클래스가 다르면 false
        if (o == null || getClass() != o.getClass()) return false;

        // 실제 비교 대상
        BookHashtagsId that = (BookHashtagsId) o;

        // book과 hashtag 두 필드가 모두 같은지 비교
        return Objects.equals(book, that.book) &&
                Objects.equals(hashtag, that.hashtag);
    }

    /**
     * hashCode() 재정의
     * equals()와 항상 함께 재정의해야 함
     * HashSet, HashMap 등에서 PK를 기준으로 올바르게 작동하게 하기 위해 필요
     */
    @Override
    public int hashCode() {
        return Objects.hash(book, hashtag); // book과 hashtag를 기반으로 해시코드 생성
    }
}
