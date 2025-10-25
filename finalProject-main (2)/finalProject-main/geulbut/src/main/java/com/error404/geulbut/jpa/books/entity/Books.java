package com.error404.geulbut.jpa.books.entity;


import com.error404.geulbut.common.BaseTimeEntity;
import com.error404.geulbut.jpa.authors.entity.Authors;
import com.error404.geulbut.jpa.categories.entity.Categories;
import com.error404.geulbut.jpa.hashtags.entity.Hashtags;
import com.error404.geulbut.jpa.publishers.entity.Publishers;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "BOOKS")
@SequenceGenerator(
        name = "SEQ_BOOKS_JPA",
        sequenceName = "SEQ_BOOKS",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "bookId", callSuper = false)
public class Books extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BOOKS_JPA")
    private Long bookId;
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Authors author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publishers publisher;

    @Lob
    private byte[] bookImg;
    private String imgUrl;
    private String nation;


    private LocalDate publishedDate;

    private Long price;
    private Long discountedPrice;
    private Long stock;
    private Long orderCount;
    private Long wishCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Categories category;

    @Lob
    private String description;

    private String isbn;
    
//    별점
    private Double rating;
    private Long reviewCount;
    

//  hashtags-books 다대다 관계 관련 - 종일
    @ManyToMany
    @BatchSize(size = 100)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(
            name="BOOK_HASHTAGS",
            joinColumns = @JoinColumn(name="BOOK_ID"),
            inverseJoinColumns = @JoinColumn(name="HASHTAG_ID")
    )
    private Set<Hashtags> hashtags = new HashSet<>();

    //    ES_DELETE_FLAG
    private String esDeleteFlag;
}


