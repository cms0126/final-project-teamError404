package com.error404.geulbut.jpa.choice.repository;

import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.choice.dto.ChoiceDto;
import com.error404.geulbut.jpa.introduction.dto.IntroductionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoiceRepository extends JpaRepository<Books, Long> {

    @Query("SELECT new com.error404.geulbut.jpa.choice.dto.ChoiceDto(" +
            "b.bookId, b.imgUrl, b.title, b.author.name, b.description) " +
            "FROM Books b " +
            "WHERE b.publishedDate IS NOT NULL " +
            "ORDER BY b.publishedDate DESC")
    Page<ChoiceDto> findChoice(Pageable pageable);

    @Query("SELECT new com.error404.geulbut.jpa.introduction.dto.IntroductionDto(" +
            "b.imgUrl, b.title, b.author.name, b.publishedDate, b.description,b.bookId) " +
            "FROM Books b " +
            "WHERE b.publishedDate IS NOT NULL " +
            "ORDER BY b.publishedDate DESC")
    Page<ChoiceRepository> findIntroductionList(Pageable pageable);

}