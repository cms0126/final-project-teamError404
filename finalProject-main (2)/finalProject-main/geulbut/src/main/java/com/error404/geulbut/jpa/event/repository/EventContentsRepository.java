package com.error404.geulbut.jpa.event.repository;

import com.error404.geulbut.jpa.event.entity.EventContents;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventContentsRepository extends JpaRepository<EventContents,Long> {
    @Query(value = "select e from  EventContents  e\n" +
            "where e.category=:searchKeyword")
    Page<EventContents> selectEventContentsList(
            @Param("searchKeyword") String searchKeyword,
            Pageable pageable
    );
}
