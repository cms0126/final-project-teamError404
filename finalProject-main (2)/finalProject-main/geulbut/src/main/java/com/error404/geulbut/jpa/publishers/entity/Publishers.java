package com.error404.geulbut.jpa.publishers.entity;

import com.error404.geulbut.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PUBLISHERS")
@SequenceGenerator(
        name = "SEQ_PUBLISHERS_JPA",
        sequenceName = "SEQ_PUBLISHERS",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "publisherId", callSuper = false)
public class Publishers extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "SEQ_PUBLISHERS_JPA")
    private Long publisherId;
    private String name;
    @Lob
    private String description;

    public Publishers(Long publisherId) {
        this.publisherId = publisherId;
    }

}
