package com.error404.geulbut.jpa.notice.entity;

import com.error404.geulbut.jpa.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "NOTICE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Oracle이면 SEQUENCE 전략 권장
    @Column(name = "NOTICE_ID")
    private Long noticeId;

    @Column(name = "WRITER", nullable = false, length = 50)
    private String writer;

    @Column(name = "TITLE", length = 300)
    private String title;

    @Column(name = "CONTENT", length = 4000)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    @Column(name = "VIEW_COUNT")
    private Long viewCount;

    @Column(name = "CATEGORY", length = 50)
    private String category;

    // FK 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private Users userId;
}
