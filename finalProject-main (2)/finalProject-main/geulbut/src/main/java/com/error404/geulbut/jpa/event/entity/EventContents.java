package com.error404.geulbut.jpa.event.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "EVENT_CONTENTS")
@SequenceGenerator(
        name = "SEQ_EVENT_CONTENTS_JPA",
        sequenceName = "SEQ_EVENT_CONTENTS",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
public class EventContents {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE
            , generator = "SEQ_EVENT_CONTENTS_JPA"
    )
    private Long id;   // 기본키 시퀀스
    private String eventImg;
    private String title;
    private String days;
    private String point;
    private String timeInfo;
    private String press;
    private String field;
    private Long price;
    private Long discount;
    private String category;
}
