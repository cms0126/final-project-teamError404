package com.error404.geulbut.jpa.qnacomment.entity;

import com.error404.geulbut.jpa.qna.entity.QnaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "QNA_COMMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qna_comment_seq")
    @SequenceGenerator(name = "qna_comment_seq", sequenceName = "QNA_COMMENT_SEQ", allocationSize = 1)
    @Column(name = "COMMENT_ID")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QNA_ID")
    private QnaEntity qna; // 어떤 QnA 글의 댓글인지

    @Column(name = "USER_ID", nullable = false)
    private String userId; // 작성자

    @Column(name = "CONTENT", length = 2000, nullable = false)
    private String content; // 댓글 내용

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;
}
