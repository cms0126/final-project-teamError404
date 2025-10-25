package com.error404.geulbut.jpa.qna.repository;

import com.error404.geulbut.jpa.qna.entity.QnaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface QnaRepository extends JpaRepository<QnaEntity, Long> {
    // ✅ 조회수 증가 (필요하면 이 방법도 사용 가능)
    @Modifying
    @Transactional
    @Query("UPDATE QnaEntity q SET q.viewCount = q.viewCount + 1 WHERE q.qnaId = :id")
    void increaseViewCount(Long id);

    // 조회수 상위 10개
    List<QnaEntity> findTop10ByOrderByViewCountDesc();
}

