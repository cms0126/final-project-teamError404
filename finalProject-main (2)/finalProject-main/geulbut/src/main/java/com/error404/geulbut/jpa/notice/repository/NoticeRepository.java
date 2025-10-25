package com.error404.geulbut.jpa.notice.repository;

import com.error404.geulbut.jpa.notice.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {
    // 조회수 증가
    @Modifying
    @Transactional
    @Query("UPDATE NoticeEntity n SET n.viewCount = n.viewCount + 1 WHERE n.noticeId = :noticeId")
    void increaseViewCount(Long noticeId);

}
