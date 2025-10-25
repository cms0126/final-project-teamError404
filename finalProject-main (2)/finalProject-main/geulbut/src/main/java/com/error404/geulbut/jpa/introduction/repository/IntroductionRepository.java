package com.error404.geulbut.jpa.introduction.repository;

import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.introduction.dto.IntroductionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntroductionRepository extends JpaRepository<Books, Long> {

    @Query("SELECT new com.error404.geulbut.jpa.introduction.dto.IntroductionDto(" +
            "b.imgUrl, b.title, b.author.name, b.publishedDate, b.description,b.bookId) " +
            "FROM Books b " +
            "WHERE b.publishedDate IS NOT NULL " +
            "ORDER BY b.publishedDate DESC")
    Page<IntroductionDto> findIntroductionList(Pageable pageable);

}
