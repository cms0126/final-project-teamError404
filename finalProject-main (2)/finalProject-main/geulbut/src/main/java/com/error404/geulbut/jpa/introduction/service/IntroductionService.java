package com.error404.geulbut.jpa.introduction.service;


import com.error404.geulbut.jpa.books.repository.BooksRepository;
import com.error404.geulbut.jpa.introduction.dto.IntroductionDto;
import com.error404.geulbut.jpa.introduction.repository.IntroductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntroductionService {

    private final IntroductionRepository introductionRepository;

    // 전체 조회 (검색/페이징 없음)
    public Page<IntroductionDto> getAllIntroductions(Pageable pageable) {
        return introductionRepository.findIntroductionList(pageable);
    }
}
