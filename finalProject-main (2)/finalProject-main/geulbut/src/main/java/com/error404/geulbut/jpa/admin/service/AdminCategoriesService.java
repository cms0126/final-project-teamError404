package com.error404.geulbut.jpa.admin.service;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.books.dto.BooksDto;
import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.books.repository.BooksRepository;
import com.error404.geulbut.jpa.categories.dto.CategoriesDto;
import com.error404.geulbut.jpa.categories.entity.Categories;
import com.error404.geulbut.jpa.categories.repository.CategoriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final MapStruct mapStruct;
    private final ErrorMsg errorMsg;
    private final BooksRepository booksRepository;

    // 전체 조회 (페이징)
    public Page<CategoriesDto> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return categoriesRepository.findAll(pageable)
                .map(mapStruct::toDto);
    }

    // 단일 조회
    public CategoriesDto getCategoryById(Long categoryId) {
        Categories category = categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(errorMsg.getMessage("error.categories.notfound")));
        return mapStruct.toDto(category);
    }

    // 등록
    public CategoriesDto createCategory(CategoriesDto categoriesDto) {
        Categories category = mapStruct.toEntity(categoriesDto);
        Categories savedCategory = categoriesRepository.save(category);
        return mapStruct.toDto(savedCategory);
    }

    // 수정
    public CategoriesDto updateCategory(CategoriesDto categoriesDto) {
        if (categoriesDto.getCategoryId() == null) {
            throw new IllegalArgumentException(errorMsg.getMessage("error.categories.id.required"));
        }

        Categories existing = categoriesRepository.findById(categoriesDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(errorMsg.getMessage("error.categories.notfound")));

        mapStruct.updateFromDto(categoriesDto, existing);
        Categories savedCategory = categoriesRepository.save(existing);
        return mapStruct.toDto(savedCategory);
    }

    // 삭제
    public boolean deleteCategory(Long categoryId) {
        if (categoriesRepository.existsById(categoryId)) {
            categoriesRepository.deleteById(categoryId);
            return true;
        }
        return false;
    }

    // 검색 기능 추가
    public Page<CategoriesDto> searchCategories(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return categoriesRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(mapStruct::toDto);
    }

    // 카테고리에 속한 책들 조회
    public List<BooksDto> getBooksByCategory(Long categoryId) {
        List<Books> books = booksRepository.findByCategory_CategoryId(categoryId);
        return books.stream()
                .map(mapStruct::toDto)
                .collect(Collectors.toList());
    }
}