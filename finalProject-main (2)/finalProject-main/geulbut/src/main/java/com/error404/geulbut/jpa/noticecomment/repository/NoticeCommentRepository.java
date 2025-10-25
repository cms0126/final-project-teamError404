package com.error404.geulbut.jpa.noticecomment.repository;

import com.error404.geulbut.jpa.notice.entity.NoticeEntity;
import com.error404.geulbut.jpa.noticecomment.entity.NoticeCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NoticeCommentRepository extends JpaRepository<NoticeCommentEntity, Long> {

    List<NoticeCommentEntity> findByNoticeOrderByCreatedAtAsc(NoticeEntity notice);
    // 페이징 조회
    Page<NoticeCommentEntity> findByNotice(NoticeEntity notice, Pageable pageable);
}
