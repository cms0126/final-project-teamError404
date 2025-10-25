package com.error404.geulbut.jpa.wishlist.entity;

import com.error404.geulbut.common.BaseTimeEntity;
import com.error404.geulbut.jpa.books.entity.Books;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "WISHLISTS",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_WISHLIST_USER_BOOK",
                        columnNames = {"USER_ID", "BOOK_ID"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"wishlistId"}, callSuper = false)  // 비즈니스 키 기준
public class Wishlist extends BaseTimeEntity {
    @Id
    private String wishlistId;
    private String userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_ID")
    private Books book;
}
