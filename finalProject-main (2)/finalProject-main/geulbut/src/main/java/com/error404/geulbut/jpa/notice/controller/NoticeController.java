package com.error404.geulbut.jpa.notice.controller;

import com.error404.geulbut.jpa.notice.dto.NoticeDto;
import com.error404.geulbut.jpa.notice.service.NoticeService;
import com.error404.geulbut.jpa.noticecomment.dto.NoticeCommentDto;
import com.error404.geulbut.jpa.noticecomment.service.NoticeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final NoticeCommentService noticeCommentService;

    // 공지 목록 - 페이징
    @GetMapping("/notice")
    public String notice(@RequestParam(value = "page", defaultValue = "1") int page,
                         @RequestParam(value = "size", defaultValue = "10") int size,
                         Model model) {
        Page<NoticeDto> pageNotices = noticeService.getNotices(page, size);
        model.addAttribute("notices", pageNotices.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", pageNotices.getTotalPages());
        return "notice/notice2";
    }

    // 공지 상세보기
    @GetMapping("/noticeText")
    public String noticeText(@RequestParam("id") Long id,
                             @RequestParam(value = "commentPage", defaultValue = "1") int commentPage,
                             Model model) {

        NoticeDto notice = noticeService.getNoticeAndIncreaseViewCount(id);
        model.addAttribute("notice", notice);

        int commentSize = 3; // 한 페이지 댓글 3개
        var commentPageData = noticeCommentService.getCommentsByNotice(id, commentPage, commentSize);

        model.addAttribute("comments", commentPageData.getContent());
        model.addAttribute("commentCurrentPage", commentPage);
        model.addAttribute("commentTotalPage", commentPageData.getTotalPages());

        return "notice/noticeText";
    }


    // 공지 작성 페이지
    @GetMapping("/noticeWrite")
    public String noticeWrite() {
        return "notice/noticeWrite";
    }

    // 공지 등록
    @PostMapping("/noticeSubmit")
    public String noticeSubmit(@RequestParam("title") String title,
                               @RequestParam("content") String content) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        NoticeDto dto = NoticeDto.builder()
                .title(title)
                .content(content)
                .writer(userId)
                .userId(userId)
                .build();
        noticeService.createNotice(dto);
        return "redirect:/notice";
    }

    // 공지 수정 페이지 이동
    @GetMapping("/noticeUpdate")
    public String noticeUpdate(@RequestParam("id") Long id, Model model) {
        NoticeDto notice = noticeService.findById(id);
        model.addAttribute("notice", notice);
        return "notice/noticeWrite"; // 글쓰기 JSP 재사용
    }

    // 공지 수정 처리
    @PostMapping("/noticeUpdateSubmit")
    public String noticeUpdateSubmit(@RequestParam("id") Long id,
                                     @RequestParam("title") String title,
                                     @RequestParam("content") String content) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        NoticeDto notice = noticeService.findById(id);

        if (!notice.getUserId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 글만 수정할 수 있습니다.");
        }

        notice.setTitle(title);
        notice.setContent(content);
        noticeService.updateNotice(notice);

        return "redirect:/noticeText?id=" + id;
    }

    // 공지 삭제 처리
    @PostMapping("/noticeDelete")
    public String noticeDelete(@RequestParam("id") Long id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        NoticeDto notice = noticeService.findById(id);

        if (!notice.getUserId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 글만 삭제할 수 있습니다.");
        }

        noticeService.deleteNotice(id);
        return "redirect:/notice";
    }
    @PostMapping("/noticeComment")
    public String addComment(NoticeCommentDto dto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        dto.setUserId(userId);

        noticeCommentService.saveComment(dto);

        return "redirect:/noticeText?id=" + dto.getNoticeId();
    }
}
