package com.error404.geulbut.jpa.categories.service;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.categories.dto.CategoriesDto;
import com.error404.geulbut.jpa.categories.entity.Categories;
import com.error404.geulbut.jpa.categories.repository.CategoriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final ErrorMsg errorMsg;
    private final MapStruct mapStruct;

    public Page<CategoriesDto> findAllCategories(Pageable pageable) {
        Page<Categories> page = categoriesRepository.findAll(pageable);
        return page.map(categories -> mapStruct.toDto(categories));
    }
}
