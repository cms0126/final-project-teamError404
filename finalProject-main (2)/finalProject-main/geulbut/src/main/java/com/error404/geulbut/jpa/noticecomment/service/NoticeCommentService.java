package com.error404.geulbut.jpa.noticecomment.service;

import com.error404.geulbut.jpa.notice.entity.NoticeEntity;
import com.error404.geulbut.jpa.notice.repository.NoticeRepository;
import com.error404.geulbut.jpa.noticecomment.dto.NoticeCommentDto;
import com.error404.geulbut.jpa.noticecomment.entity.NoticeCommentEntity;
import com.error404.geulbut.jpa.noticecomment.repository.NoticeCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeCommentService {

    private final NoticeCommentRepository noticeCommentRepository;
    private final NoticeRepository noticeRepository;

    // 댓글 저장
    public void saveComment(NoticeCommentDto dto) {
        NoticeEntity notice = noticeRepository.findById(dto.getNoticeId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 공지사항입니다."));

        NoticeCommentEntity comment = NoticeCommentEntity.builder()
                .notice(notice)
                .userId(dto.getUserId())
                .content(dto.getContent())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        noticeCommentRepository.save(comment);
    }

    // 특정 공지 댓글 조회
    @Transactional(readOnly = true)
    public List<NoticeCommentDto> getCommentsByNotice(Long noticeId) {
        NoticeEntity notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 공지사항입니다."));

        return noticeCommentRepository.findByNoticeOrderByCreatedAtAsc(notice)
                .stream()
                .map(c -> NoticeCommentDto.builder()
                        .commentId(c.getCommentId())
                        .noticeId(c.getNotice().getNoticeId())
                        .userId(c.getUserId())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // -------------------- 여기서부터 페이징 기능 추가 --------------------
    @Transactional(readOnly = true)
    public Page<NoticeCommentDto> getCommentsByNotice(Long noticeId, int page, int size) {
        NoticeEntity notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 공지사항입니다."));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").ascending());

        Page<NoticeCommentEntity> commentPage = noticeCommentRepository.findByNotice(notice, pageable);

        return commentPage.map(c -> NoticeCommentDto.builder()
                .commentId(c.getCommentId())
                .noticeId(c.getNotice().getNoticeId())
                .userId(c.getUserId())
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build());
    }
    // 댓글 수정
    public void updateComment(Long commentId, String userId, String content) {
        NoticeCommentEntity comment = noticeCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글입니다."));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("본인 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(content);
        comment.setUpdatedAt(new Date());
        noticeCommentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, String userId) {
        NoticeCommentEntity comment = noticeCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글입니다."));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("본인 댓글만 삭제할 수 있습니다.");
        }

        noticeCommentRepository.delete(comment);
    }
    // 댓글 삭제/수정용: 댓글에서 noticeId 조회
    @Transactional(readOnly = true)
    public Long getNoticeIdByComment(Long commentId) {
        NoticeCommentEntity comment = noticeCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글입니다."));
        return comment.getNotice().getNoticeId();
    }


}