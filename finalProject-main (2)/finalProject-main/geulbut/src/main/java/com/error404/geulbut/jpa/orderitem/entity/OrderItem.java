package com.error404.geulbut.jpa.orderitem.entity;

import com.error404.geulbut.common.BaseTimeEntity;
import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.orders.entity.Orders;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ORDERED_ITEM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ORDERED_ITEM")
    @SequenceGenerator(name = "SEQ_ORDERED_ITEM", sequenceName = "SEQ_ORDERED_ITEM", allocationSize = 1)
    private Long orderedItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_ID")
    private Books book;

    private int quantity;
    private Long price;
    private Long discountedPrice;
}

