package com.error404.geulbut.jpa.reviews.entity;

import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "REVIEWS")
public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "dno")           // DB FK 컬럼명 작성
//    private Dept dept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Books book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
    private int rating;

    @Column(length = 2000)
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long orderedItemId;
}