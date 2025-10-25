package com.error404.geulbut.jpa.admin.controller;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.jpa.admin.service.AdminPublishersService;
import com.error404.geulbut.jpa.books.dto.BooksDto;
import com.error404.geulbut.jpa.publishers.dto.PublishersDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/publishers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPublishersController {
    private final AdminPublishersService adminPublishersService;
    private final ErrorMsg errorMsg;

    //    출판사 목록 검색
    @GetMapping
    public String listPublishersPage(Model model,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) String keyword) {
        Page<PublishersDto> publishersPage;

        if (keyword == null || keyword.isEmpty()) {
            publishersPage = adminPublishersService.getAllPublishers(page, size);
        } else {
            publishersPage = adminPublishersService.searchPublishers(keyword, page, size);
            model.addAttribute("keyword", keyword);
        }

        model.addAttribute("publishersPage", publishersPage);
        model.addAttribute("keyword", keyword);

        return "admin/admin_publishers_list";
    }

    //        단일 출판사 조회
    @GetMapping("/{publisherId}")
    @ResponseBody
    public PublishersDto getPublishersById(@PathVariable("publisherId") Long publisherId) {
        return adminPublishersService.getPublisherById(publisherId);
    }

//    출판사 등록
    @PostMapping
    @ResponseBody
    public PublishersDto createPublisher(@Valid @RequestBody PublishersDto publishersDto) {
        return adminPublishersService.savePublisher(publishersDto);
    }

//    출판사 수정
    @PutMapping("/{publisherId}")
    @ResponseBody
    public PublishersDto updatePublisher(@PathVariable Long publisherId,
                                         @RequestBody PublishersDto publishersDto) {
        publishersDto.setPublisherId(Long.valueOf(publisherId));
        return adminPublishersService.updatePublisher(publishersDto);
    }

//    출판사 삭제
    @DeleteMapping("/{publisherId}")
    @ResponseBody
    public boolean deletePublisher(@PathVariable Long publisherId) {
        return adminPublishersService.deletePublisher(publisherId);
    }

    // 출판사별 책 조회
    @GetMapping("/{publisherId}/books")
    @ResponseBody
    public List<BooksDto> getBooksByPublisher(@PathVariable Long publisherId) {
        return adminPublishersService.getBooksByPublisher(publisherId);
    }
}
