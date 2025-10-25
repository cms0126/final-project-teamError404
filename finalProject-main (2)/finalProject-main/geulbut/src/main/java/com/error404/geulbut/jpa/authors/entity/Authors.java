package com.error404.geulbut.jpa.authors.entity;

import com.error404.geulbut.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AUTHORS")
@SequenceGenerator(
        name = "SEQ_AUTHORS_JPA",
        sequenceName = "SEQ_AUTHORS",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "authorId", callSuper = false)
public class Authors extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "SEQ_AUTHORS_JPA")
    private Long authorId;
    private String name;
    @Lob
    private String description;
    @Lob
    private byte[] authorImg;
    private String imgUrl;

    public Authors(Long authorId) {
        this.authorId = authorId;
    }

}
