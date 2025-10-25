package com.error404.geulbut.jpa.event.service;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.event.entity.EventContents;
import com.error404.geulbut.jpa.event.repository.EventContentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventContentsService {
    private final EventContentsRepository eventContentsRepository;
    private final MapStruct mapStruct;
    private final ErrorMsg errorMsg;

    public Page<EventContents> selectEventContentsListA(Pageable pageable) {
        Page<EventContents> page= eventContentsRepository.selectEventContentsList("A", pageable);
        return page;
    }
    public Page<EventContents> selectEventContentsListB(Pageable pageable) {
        Page<EventContents> page= eventContentsRepository.selectEventContentsList("B", pageable);
        return page;
    }
}
