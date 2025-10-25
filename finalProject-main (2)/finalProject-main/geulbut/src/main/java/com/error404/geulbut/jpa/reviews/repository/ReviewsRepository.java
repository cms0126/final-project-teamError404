package com.error404.geulbut.jpa.reviews.repository;

import com.error404.geulbut.jpa.reviews.entity.Reviews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long> {

    // === 중복 리뷰 체크 ===
    // 옵션 A (정석): 메서드명으로 경로 표현
    // boolean existsByUser_UserIdAndOrderedItemId(String userId, Long orderedItemId);

    // 옵션 B (핫픽스): @Query로 경로 명시 — 서비스 코드 그대로 유지 가능
    @Query("""
           select count(r) > 0
             from Reviews r
            where r.user.userId = :userId
              and r.orderedItemId = :orderedItemId
           """)
    boolean existsByUserIdAndOrderedItemId(@Param("userId") String userId,
                                           @Param("orderedItemId") Long orderedItemId);

    // === 평점 분포 (1~5) ===
    @Query("""
           select r.rating as rating, count(r) as cnt
             from Reviews r
            where r.book.bookId = :bookId
            group by r.rating
           """)
    List<RatingCount> countByRating(@Param("bookId") Long bookId);

    // === 최신 리뷰 페이징 ===
    @Query("""
           select r
             from Reviews r
            where r.book.bookId = :bookId
            order by r.createdAt desc
           """)
    Page<Reviews> findRecentByBookId(@Param("bookId") Long bookId, Pageable pageable);
}
