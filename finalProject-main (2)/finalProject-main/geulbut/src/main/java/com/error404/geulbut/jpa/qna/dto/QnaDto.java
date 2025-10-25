package com.error404.geulbut.jpa.qna.dto;

import com.error404.geulbut.jpa.qnacomment.dto.QnaCommentDto;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaDto {
    private Long id;
    private String title;
    private String qContent;
    private Date  qAt;
    private String aId;//로그인 된 유저 아이디
    private String aContent;//로그인 된 유저가 단 댓글
    private Date  aAt; // 로그인 된 유저가 댓글 단 시간
    private Long viewCount;
    private String userId;
    // 추가: 댓글 리스트
    private List<QnaCommentDto> comments;
}

