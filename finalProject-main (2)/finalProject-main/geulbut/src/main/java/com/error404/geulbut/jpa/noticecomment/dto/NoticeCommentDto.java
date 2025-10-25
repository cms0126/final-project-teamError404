package com.error404.geulbut.jpa.noticecomment.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeCommentDto {
    private Long commentId;
    private Long noticeId;
    private String userId;
    private String content;
    private Date createdAt;
    private Date updatedAt;
}
