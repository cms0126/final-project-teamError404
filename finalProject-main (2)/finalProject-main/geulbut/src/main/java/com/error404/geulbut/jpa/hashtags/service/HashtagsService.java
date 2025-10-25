package com.error404.geulbut.jpa.hashtags.service;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.hashtags.dto.HashtagsDto;
import com.error404.geulbut.jpa.hashtags.entity.Hashtags;
import com.error404.geulbut.jpa.hashtags.repository.HashtagsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashtagsService {
    private final HashtagsRepository hashtagsRepository;
    private final ErrorMsg errorMsg;
    private final MapStruct mapStruct;

    public Page<HashtagsDto> findAllHashtags(Pageable pageable) {
        Page<Hashtags> page = hashtagsRepository.findAll(pageable);
        return page.map(hashtags -> mapStruct.toDto(hashtags));
    }
}
