package com.error404.geulbut.jpa.reviews.repository;

/**
 * 평점 분포(GroupBy 결과)를 받을 Projection 인터페이스
 * JPQL 또는 NativeQuery에서
 *   SELECT r.rating AS rating, COUNT(r) AS cnt
 * 형태로 불러온 결과를 자동 맵핑함
 */
public interface RatingCount {
    Integer getRating();  // r.rating 값 (1~5)
    Long getCnt();        // COUNT(*) 값
}
