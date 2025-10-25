package com.error404.geulbut.jpa.hashtags.repository;

import com.error404.geulbut.jpa.hashtags.entity.Hashtags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HashtagsRepository extends JpaRepository<Hashtags,Long> {

    // 기존 이름 검색
    Page<Hashtags> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // 아이디로 검색
    Optional<Hashtags> findByHashtagId(Long hashtagId);

}
