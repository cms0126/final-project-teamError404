package com.error404.geulbut.jpa.hotdeal.service;

import com.error404.geulbut.jpa.hotdeal.dto.HotdealDto;
import com.error404.geulbut.jpa.hotdeal.repository.HotdelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotdealService {

    private final HotdelRepository hotdelRepository;

    // 전체 조회 (페이징 포함)
    public Page<HotdealDto> getAllHotdeal(Pageable pageable) {
        return hotdelRepository.findHotdealList(pageable);
    }
}
