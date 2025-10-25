package com.error404.geulbut.jpa.notice.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDto {
    private Long noticeId;
    private String writer;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private Long viewCount;
    private String category;
    private String userId; // FK (UsersEntity)
}
