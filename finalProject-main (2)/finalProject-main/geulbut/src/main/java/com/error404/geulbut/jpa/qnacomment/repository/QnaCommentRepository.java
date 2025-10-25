package com.error404.geulbut.jpa.qnacomment.repository;

import com.error404.geulbut.jpa.qna.entity.QnaEntity;
import com.error404.geulbut.jpa.qnacomment.entity.QnaCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaCommentRepository extends JpaRepository<QnaCommentEntity, Long> {
    // QnA 글에 달린 댓글을 생성일 기준 오름차순으로 조회
    List<QnaCommentEntity> findByQnaOrderByCreatedAtAsc(QnaEntity qna);
    Page<QnaCommentEntity> findByQna(QnaEntity qna, Pageable pageable);
}
