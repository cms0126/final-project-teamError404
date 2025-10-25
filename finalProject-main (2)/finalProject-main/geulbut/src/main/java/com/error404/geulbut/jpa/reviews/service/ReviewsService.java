package com.error404.geulbut.jpa.reviews.service;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.books.repository.BooksRepository;
import com.error404.geulbut.jpa.reviews.dto.ReviewsDto;
import com.error404.geulbut.jpa.reviews.entity.Reviews;
import com.error404.geulbut.jpa.reviews.repository.ReviewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ReviewsService {

    private final ReviewsRepository reviewsRepository;
    private final BooksRepository booksRepository;
    private final MapStruct mapStruct;
    private final ErrorMsg errorMsg;


    @Transactional
    public void saveReview(ReviewsDto reviewsDto) {

        // 1) 중복 리뷰 방지 (userId + orderedItemId 유니크 정책)
        boolean exists = reviewsRepository.existsByUserIdAndOrderedItemId(
                reviewsDto.getUserId(), reviewsDto.getOrderedItemId());
        if (exists) {
            // 컨트롤러에서 409로 변환해서 내려주면 프론트가 정확히 분기 가능
            throw new IllegalArgumentException("duplicate");
        }

        // 2) 도서 존재 확인 (없으면 404 성격 오류)
        Books book = booksRepository.findById(reviewsDto.getBookId())
                .orElseThrow(() -> new RuntimeException(errorMsg.getMessage("errors.not.found")));

        // 3) 리뷰 저장
        Reviews review = mapStruct.toEntity(reviewsDto);
        // createdAt/updatedAt을 엔티티에서 @PrePersist/@PreUpdate로 관리하지 않는다면 주석 해제
        // review.setCreatedAt(LocalDateTime.now());
        // review.setUpdatedAt(LocalDateTime.now());
        reviewsRepository.save(review);

        // 4) BOOKS 집계 원자적 갱신 (경합에도 안전)
        //  - UPDATE BOOKS
        //      SET REVIEW_COUNT = REVIEW_COUNT + 1,
        //          RATING = (RATING * REVIEW_COUNT + :newRating) / (REVIEW_COUNT + 1)
        //    WHERE BOOK_ID = :bookId
        int updated = booksRepository.applyReviewAggregate(reviewsDto.getBookId(), reviewsDto.getRating());
        if (updated != 1) {
            // 이 상황은 거의 없지만 안전망
            throw new IllegalStateException("aggregate-failed");
        }

        // mapStruct로 Books 엔티티를 다시 set하고 save하는 방식은 제거
        // (동시성 시 기존 평균을 다시 읽어 계산하면 경합에 취약)
    }
}
