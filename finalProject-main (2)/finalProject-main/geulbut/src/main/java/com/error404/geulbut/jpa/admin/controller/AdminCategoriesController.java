package com.error404.geulbut.jpa.admin.controller;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.jpa.admin.service.AdminCategoriesService;
import com.error404.geulbut.jpa.books.dto.BooksDto;
import com.error404.geulbut.jpa.categories.dto.CategoriesDto;
import com.error404.geulbut.jpa.categories.repository.CategoriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoriesController {

    private final AdminCategoriesService adminCategoriesService;
    private final ErrorMsg errorMsg;

    // 카테고리 목록 페이지 (HTML 렌더링)
    @GetMapping
    public String listCategoriesPage(Model model,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) String keyword) {

        Page<CategoriesDto> categoriesPage;

        if (keyword == null || keyword.isEmpty()) {
            categoriesPage = adminCategoriesService.getAllCategories(page, size);
        } else {
            // 검색 기능을 나중에 추가할 수도 있음
            categoriesPage = adminCategoriesService.searchCategories(keyword, page, size);
            model.addAttribute("keyword", keyword); // 검색어 유지
        }

        model.addAttribute("categoriesPage", categoriesPage);
        model.addAttribute("keyword", keyword); // 검색어 유지
        return "admin/admin_categories_list"; // JSP 경로 반환
    }


    // 단일 조회 (AJAX)
    @GetMapping("/{categoryId}")
    @ResponseBody
    public CategoriesDto getCategory(@PathVariable Long categoryId) {
        return adminCategoriesService.getCategoryById(categoryId);
    }

    // 등록
    @PostMapping
    @ResponseBody
    public CategoriesDto createCategory(@RequestBody CategoriesDto categoriesDto) {
        return adminCategoriesService.createCategory(categoriesDto);
    }

    // 수정
    @PutMapping("/{categoryId}")
    @ResponseBody
    public CategoriesDto updateCategory(@PathVariable Long categoryId,
                                        @RequestBody CategoriesDto categoriesDto) {
        categoriesDto.setCategoryId(categoryId);
        return adminCategoriesService.updateCategory(categoriesDto);
    }

    // 삭제
    @DeleteMapping("/{categoryId}")
    @ResponseBody
    public boolean deleteCategory(@PathVariable Long categoryId) {
        return adminCategoriesService.deleteCategory(categoryId);
    }

    // 카테고리에 속한 책들 조회
    @GetMapping("/{id}/books")
    public ResponseEntity<List<BooksDto>> getBooksByCategory(@PathVariable Long id) {
        return ResponseEntity.ok(adminCategoriesService.getBooksByCategory(id));
    }
}
