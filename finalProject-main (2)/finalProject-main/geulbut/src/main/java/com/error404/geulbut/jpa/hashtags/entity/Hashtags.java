package com.error404.geulbut.jpa.hashtags.entity;

import com.error404.geulbut.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "HASHTAGS")
@SequenceGenerator(
        name = "SEQ_HASHTAGS_JPA",
        sequenceName = "SEQ_HASHTAGS",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "hashtagId", callSuper = false)
public class Hashtags extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "SEQ_HASHTAGS_JPA")
    private Long hashtagId;
    private String name;
}
