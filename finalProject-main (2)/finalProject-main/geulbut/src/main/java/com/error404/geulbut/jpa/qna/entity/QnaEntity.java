package com.error404.geulbut.jpa.qna.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "QNA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qna_seq")
    @SequenceGenerator(name = "qna_seq", sequenceName = "QNA_SEQ", allocationSize = 1)
    @Column(name = "QNA_ID")
    private Long qnaId;

    @Column(name = "TITLE", length = 300)
    private String title;

    @Column(name = "Q_CONTENT", length = 4000)
    private String qContent;

    @Column(name = "Q_AT")
    private Date qAt;

    @Column(name = "A_ID", length = 50)
    private String aId;

    @Column(name = "A_CONTENT", length = 4000)
    private String aContent;

    @Column(name = "A_AT")
    private Date aAt;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;
    // ✅ 조회수 추가
    @Column(name = "Q_VIEW_COUNT")
    private Long viewCount;

    @Column(name = "USER_ID", length = 50, nullable = false)
    private String userId;
}
