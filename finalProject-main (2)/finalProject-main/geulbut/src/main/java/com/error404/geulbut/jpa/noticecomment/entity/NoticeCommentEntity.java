package com.error404.geulbut.jpa.noticecomment.entity;

import com.error404.geulbut.jpa.notice.entity.NoticeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "NOTICE_COMMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notice_comment_seq")
    @SequenceGenerator(name = "notice_comment_seq", sequenceName = "NOTICE_COMMENT_SEQ", allocationSize = 1)
    @Column(name = "COMMENT_ID")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NOTICE_ID")
    private NoticeEntity notice;

    @Column(name = "USER_ID", length = 50, nullable = false)
    private String userId;

    @Column(name = "CONTENT", length = 2000)
    private String content;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;
}
