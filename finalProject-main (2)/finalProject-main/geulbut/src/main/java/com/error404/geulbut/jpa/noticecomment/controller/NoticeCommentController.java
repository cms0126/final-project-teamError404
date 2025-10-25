package com.error404.geulbut.jpa.noticecomment.controller;

import com.error404.geulbut.jpa.noticecomment.service.NoticeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class NoticeCommentController {

    private final NoticeCommentService noticeCommentService;

    // 댓글 수정
    @PostMapping("/noticeCommentUpdate")
    public String updateComment(@RequestParam Long commentId,
                                @RequestParam String content,
                                @AuthenticationPrincipal UserDetails user) {

        // 댓글 수정
        noticeCommentService.updateComment(commentId, user.getUsername(), content);

        // 수정된 댓글 엔티티에서 noticeId 가져오기
        Long noticeId = noticeCommentService.getNoticeIdByComment(commentId);

        return "redirect:/noticeText?id=" + noticeId;
    }

    // 댓글 삭제
    @PostMapping("/noticeCommentDelete")
    public String deleteComment(@RequestParam Long commentId,
                                @AuthenticationPrincipal UserDetails user) {

        // 삭제할 댓글에서 noticeId 가져오기
        Long noticeId = noticeCommentService.getNoticeIdByComment(commentId);

        // 댓글 삭제
        noticeCommentService.deleteComment(commentId, user.getUsername());

        return "redirect:/noticeText?id=" + noticeId;
    }
}
