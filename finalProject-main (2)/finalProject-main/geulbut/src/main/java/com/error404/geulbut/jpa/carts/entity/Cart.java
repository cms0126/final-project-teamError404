package com.error404.geulbut.jpa.carts.entity;

import com.error404.geulbut.common.BaseTimeEntity;
import com.error404.geulbut.jpa.books.entity.Books;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "CARTS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"USER_ID", "BOOK_ID"})
        },
        indexes = {
                @Index(name = "IDX_CARTS_USER", columnList = "USER_ID"),
                @Index(name = "IDX_CARTS_BOOK", columnList = "BOOK_ID")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "cartId", callSuper = false)
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CARTS_JPA")
    @SequenceGenerator(
            name = "SEQ_CARTS_JPA",
            sequenceName = "SEQ_CARTS",
            allocationSize = 1
    )
    @Column(name = "CART_ID")
    private Long cartId;

    @Column(name = "USER_ID", nullable = false)
    private String userId;

    // 연관관계 (BOOK_ID FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_ID", nullable = false)
    private Books book;

    @Column(nullable = false)
    private int quantity;
}
