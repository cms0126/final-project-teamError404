package com.error404.geulbut.jpa.authors.repository;


import com.error404.geulbut.jpa.authors.entity.Authors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorsRepository extends JpaRepository<Authors, Long> {
//    작가검색키워드
    Page<Authors> findByNameContainingIgnoreCase(String name, Pageable pageable);



}
