package com.error404.geulbut.jpa.qnacomment.controller;

import com.error404.geulbut.jpa.qnacomment.service.QnaCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class QnaCommentController {
    private final QnaCommentService qnaCommentService;

    @PostMapping("/qnaComment")
    public String addComment(@RequestParam("id") Long qnaId,
                             @RequestParam("aContent") String content) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        qnaCommentService.addComment(qnaId, userId, content);
        return "redirect:/qnaText?id=" + qnaId;
    }
    @PostMapping("/qnaCommentDelete")
    public String deleteComment(@RequestParam Long commentId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Long qnaId = qnaCommentService.deleteCommentAndGetQnaId(commentId, userId);
        return "redirect:/qnaText?id=" + qnaId;
    }
    // 댓글 수정
    @PostMapping("/qnaCommentUpdate")
    public String updateComment(@RequestParam("commentId") Long commentId,
                                @RequestParam("content") String content) {
        qnaCommentService.updateComment(commentId, content);
        Long qnaId = qnaCommentService.findQnaIdByComment(commentId);
        return "redirect:/qnaText?id=" + qnaId;
    }



}
