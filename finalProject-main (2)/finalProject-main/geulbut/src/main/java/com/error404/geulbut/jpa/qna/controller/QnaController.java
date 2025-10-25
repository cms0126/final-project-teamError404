package com.error404.geulbut.jpa.qna.controller;

import com.error404.geulbut.jpa.qna.dto.QnaDto;
import com.error404.geulbut.jpa.qna.entity.QnaEntity;
import com.error404.geulbut.jpa.qna.service.QnaService;
import com.error404.geulbut.jpa.qnacomment.dto.QnaCommentDto;
import com.error404.geulbut.jpa.qnacomment.service.QnaCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class QnaController {
    private final QnaService qnaService;
    private final QnaCommentService qnaCommentService;

    // QnA 목록 - 페이징 적용
    @GetMapping("/qna")
    public String qnaList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        var pageQnas = qnaService.getQnas(page, size); // Page<QnaDto>
        List<QnaDto> qnas = pageQnas.getContent();

        // 각 QnaDto에 첫 번째 댓글(답변자) 세팅
        for (QnaDto qna : qnas) {
            List<QnaCommentDto> comments = qnaCommentService.getCommentsByQna(qna.getId());
            qna.setComments(comments); // comments 필드에 세팅
        }

        model.addAttribute("qnas", pageQnas.getContent()); // 현재 페이지 글 목록
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", pageQnas.getTotalPages());

        return "qna/qna";
    }


    // QnA 상세보기
    @GetMapping("/qnaText")
    public String qnaText(
            @RequestParam("id") Long id,
            @RequestParam(value = "commentPage", defaultValue = "1") int commentPage,
            Model model) {

        // 글 조회 + 조회수 증가
        QnaDto qna = qnaService.getQnaAndIncreaseViewCount(id);
        model.addAttribute("qna", qna);

        // 댓글 페이징 조회 (한 페이지에 5개씩)
        int pageSize = 3;
        var pageComments = qnaCommentService.getCommentsByQnaPaged(id, commentPage, pageSize);

        model.addAttribute("comments", pageComments.getContent());
        model.addAttribute("commentCurrentPage", commentPage);
        model.addAttribute("commentTotalPage", pageComments.getTotalPages());

        // 댓글 전체 개수
        model.addAttribute("totalCommentCount", pageComments.getTotalElements());

        return "qna/qnaText";
    }



    // QnA 글쓰기 페이지
    @GetMapping("/qnaWrite")
    public String qnaWrite() {
        return "qna/qnaWrite";
    }

    // QnA 글쓰기 제출
    @PostMapping("/qnaSubmit")
    public String submitQna(QnaDto qnaDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        qnaDto.setUserId(userId);

        if (qnaDto.getId() == null) {
            // 새 글 작성
            qnaService.saveQna(qnaDto);
        } else {
            // 기존 글 수정
            qnaService.updateQna(qnaDto);
        }

        return "redirect:/qna";
    }

    // Qna 수정페이지 이동
    @GetMapping("/qnaUpdate")
    public String qnaUpdate(@RequestParam("id") Long id, Model model) {
        QnaDto qna = qnaService.findById(id);  // 기존 글 가져오기
        model.addAttribute("qna", qna);        // JSP에서 qna로 사용
        return "qna/qnaWrite";                 // 수정 JSP
    }

    // 삭제 처리
    @PostMapping("/qnaDelete")
    public String deleteQna(@RequestParam("id") Long id) {
        // 로그인한 사용자 정보 가져오기
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        QnaDto qna = qnaService.findById(id);

        // 작성자와 로그인 사용자 일치 확인
        if (!qna.getUserId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 글만 삭제할 수 있습니다.");
        }

        // 삭제
        qnaService.deleteQna(id);

        return "redirect:/qna";
    }

    // 자주 묻는 질문 (조회수 TOP 10)
    @GetMapping("/commonquestions")
    public String getCommonQuestions(Model model) {
        List<QnaEntity> topEntities = qnaService.getTop10Qna();
        List<QnaDto> topQnas = topEntities.stream().map(entity -> QnaDto.builder()
                        .id(entity.getQnaId())
                        .title(entity.getTitle())
                        .qAt(entity.getQAt())
                        .viewCount(entity.getViewCount())
                        .userId(entity.getUserId())
                        .build())
                .collect(Collectors.toList());
        model.addAttribute("topQnas", topQnas);
        return "commonquestions/commonquestions";  // JSP
    }

}
