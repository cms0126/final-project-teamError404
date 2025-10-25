package com.error404.geulbut.jpa.categories.repository;

import com.error404.geulbut.jpa.categories.entity.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {

    Page<Categories> findByNameContainingIgnoreCase(String name, Pageable pageable);


}
