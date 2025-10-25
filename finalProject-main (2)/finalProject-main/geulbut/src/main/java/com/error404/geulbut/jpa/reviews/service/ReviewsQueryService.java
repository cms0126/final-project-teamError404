package com.error404.geulbut.jpa.reviews.service;

import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.books.repository.BooksRepository;
import com.error404.geulbut.jpa.reviews.dto.ReviewsSummaryDto;
import com.error404.geulbut.jpa.reviews.entity.Reviews;
import com.error404.geulbut.jpa.reviews.repository.RatingCount;
import com.error404.geulbut.jpa.reviews.repository.ReviewsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewsQueryService {

    private final ReviewsRepository reviewsRepository;
    private final BooksRepository booksRepository;

    @Transactional(readOnly = true)
    public ReviewsSummaryDto getSummary(Long bookId) {
        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("book not found"));

        ReviewsSummaryDto dto = new ReviewsSummaryDto();
        dto.setTotal(book.getReviewCount() == null ? 0 : book.getReviewCount());
        dto.setAvg(book.getRating() == null ? 0 : round(book.getRating(), 2));

        // 분포(1~5점) 집계
        List<RatingCount> rows = reviewsRepository.countByRating(bookId);
        long c1=0,c2=0,c3=0,c4=0,c5=0;
        for (RatingCount rc : rows) {
            int rating = rc.getRating();
            long cnt = rc.getCnt();
            switch (rating) {
                case 1 -> c1 = cnt;
                case 2 -> c2 = cnt;
                case 3 -> c3 = cnt;
                case 4 -> c4 = cnt;
                case 5 -> c5 = cnt;
            }
        }
        dto.setC1(c1); dto.setC2(c2); dto.setC3(c3); dto.setC4(c4); dto.setC5(c5);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<Reviews> getRecentShortReviews(Long bookId, int limit) {
        return reviewsRepository.findRecentByBookId(bookId, PageRequest.of(0, limit))
                .getContent();
    }

    private static double round(double v, int scale) {
        double p = Math.pow(10, scale);
        return Math.round(v * p) / p;
    }
    @PersistenceContext
    private final EntityManager em;

    public double getAverageRating(Long bookId) {
        Double avg = em.createQuery(
                        "select avg(r.rating) from Reviews r where r.book.bookId = :bookId", Double.class)
                .setParameter("bookId", bookId)
                .getSingleResult();
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0; // 소수 1자리
    }

    public long getReviewCount(Long bookId) {
        Long cnt = em.createQuery(
                        "select count(r) from Reviews r where r.book.bookId = :bookId", Long.class)
                .setParameter("bookId", bookId)
                .getSingleResult();
        return cnt != null ? cnt : 0L;
    }
}
