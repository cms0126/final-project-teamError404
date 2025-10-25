package com.error404.geulbut.jpa.publishers.service;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.publishers.dto.PublishersDto;
import com.error404.geulbut.jpa.publishers.entity.Publishers;
import com.error404.geulbut.jpa.publishers.repository.PublishersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublishersService {
    private final PublishersRepository publishersRepository;
    public final ErrorMsg errorMsg;
    private final MapStruct mapStruct;

    public Page<PublishersDto> findAllPublishers(Pageable pageable) {
        Page<Publishers> page = publishersRepository.findAll(pageable);
        return page.map(publishers ->  mapStruct.toDto(publishers));
    }
}
