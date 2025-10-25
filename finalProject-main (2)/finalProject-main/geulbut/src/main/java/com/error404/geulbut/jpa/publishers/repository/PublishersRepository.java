package com.error404.geulbut.jpa.publishers.repository;

import com.error404.geulbut.jpa.publishers.entity.Publishers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PublishersRepository extends JpaRepository<Publishers,Long> {

//    출판사검색(이름, 출판사id)
    @Query("SELECT p FROM Publishers p WHERE p.publisherId = :id OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Publishers> searchByIdOrName(@Param("id") Long id, @Param("keyword") String keyword, Pageable pageable);


}
