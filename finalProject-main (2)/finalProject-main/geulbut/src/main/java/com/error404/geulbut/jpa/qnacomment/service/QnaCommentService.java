package com.error404.geulbut.jpa.qnacomment.service;

import com.error404.geulbut.jpa.qna.entity.QnaEntity;
import com.error404.geulbut.jpa.qna.repository.QnaRepository;
import com.error404.geulbut.jpa.qnacomment.dto.QnaCommentDto;
import com.error404.geulbut.jpa.qnacomment.entity.QnaCommentEntity;
import com.error404.geulbut.jpa.qnacomment.repository.QnaCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QnaCommentService {
    private final QnaCommentRepository qnaCommentRepository;
    private final QnaRepository qnaRepository;

    @Transactional
    public void addComment(Long qnaId, String userId, String content) {
        QnaEntity qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 글입니다."));

        QnaCommentEntity comment = QnaCommentEntity.builder()
                .qna(qna)
                .userId(userId)
                .content(content)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        qnaCommentRepository.save(comment);
    }

    public List<QnaCommentDto> getCommentsByQna(Long qnaId) {
        QnaEntity qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 글입니다."));

        return qnaCommentRepository.findByQnaOrderByCreatedAtAsc(qna)
                .stream()
                .map(entity -> new QnaCommentDto(
                        entity.getCommentId(),
                        entity.getQna().getQnaId(),
                        entity.getUserId(),
                        entity.getContent(),
                        entity.getCreatedAt(),
                        entity.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public Page<QnaCommentDto> getCommentsByQnaPaged(Long qnaId, int page, int size) {
        QnaEntity qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 글입니다."));

        Page<QnaCommentEntity> commentPage = qnaCommentRepository.findByQna(
                qna, PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "createdAt"))
        );

        return commentPage.map(entity -> new QnaCommentDto(
                entity.getCommentId(),
                entity.getQna().getQnaId(),
                entity.getUserId(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        ));
    }
    @Transactional
    public Long deleteCommentAndGetQnaId(Long commentId, String userId) {
        QnaCommentEntity comment = qnaCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("본인 댓글만 삭제할 수 있습니다.");
        }

        Long qnaId = comment.getQna().getQnaId(); // 삭제 전에 qnaId 가져오기
        qnaCommentRepository.delete(comment);
        return qnaId;
    }

    @Transactional(readOnly = true)
    public Long findQnaIdByComment(Long commentId) {
        QnaCommentEntity comment = qnaCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));
        return comment.getQna().getQnaId();
    }
    public void updateComment(Long commentId, String content) {
        QnaCommentEntity comment = qnaCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));
        comment.setContent(content);
        comment.setUpdatedAt(new Date());
        qnaCommentRepository.save(comment);
    }



}
