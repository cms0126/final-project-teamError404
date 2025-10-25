package com.error404.geulbut.jpa.qnacomment.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaCommentDto{
    private Long commentId;   // 댓글 PK
    private Long qnaId;       // 어느 QnA 글인지
    private String userId;    // 작성자
    private String content;   // 댓글 내용
    private Date createdAt;   // 작성 시간
    private Date updatedAt;   // 수정 시간
}
