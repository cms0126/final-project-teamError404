package com.error404.geulbut.jpa.hotdeal.repository;

import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.hotdeal.dto.HotdealDto;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.error404.geulbut.jpa.hotdeal.dto.HotdealDto;
import com.error404.geulbut.jpa.books.entity.Books;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HotdelRepository extends JpaRepository<Books, Long>{

    @Query("SELECT new com.error404.geulbut.jpa.hotdeal.dto.HotdealDto(" +
            "b.bookId, b.imgUrl, b.title, b.author.name, b.price, b.discountedPrice) " +
            "FROM Books b " +
            "WHERE b.discountedPrice IS NOT NULL " +
            "ORDER BY b.discountedPrice ASC")
    Page<HotdealDto> findHotdealList(Pageable pageable);

}