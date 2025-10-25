package com.error404.geulbut.jpa.choice.service;


import com.error404.geulbut.jpa.choice.dto.ChoiceDto;
import com.error404.geulbut.jpa.choice.repository.ChoiceRepository;
import com.error404.geulbut.jpa.introduction.dto.IntroductionDto;
import com.error404.geulbut.jpa.introduction.repository.IntroductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChoiceService {

    private final ChoiceRepository choiceRepository;

    // 전체 조회 (검색/페이징 없음)

    public Page<ChoiceDto> getAllChoice(Pageable pageable) {
        return choiceRepository.findChoice(pageable);
    }
}
