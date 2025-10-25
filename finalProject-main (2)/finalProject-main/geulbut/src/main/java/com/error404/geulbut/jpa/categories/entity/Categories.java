package com.error404.geulbut.jpa.categories.entity;

import com.error404.geulbut.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CATEGORIES")
@SequenceGenerator(
        name = "SEQ_CATEGORIES_JPA",
        sequenceName = "SEQ_CATEGORIES",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "categoryId", callSuper = false)
public class Categories extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "SEQ_CATEGORIES_JPA")
    private Long categoryId;
    private String name;
    public Categories(Long categoryId) {
        this.categoryId = categoryId;
    }

}
