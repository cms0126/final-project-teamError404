package com.error404.geulbut.jpa.notice.service;

import com.error404.geulbut.jpa.notice.dto.NoticeDto;
import com.error404.geulbut.jpa.notice.entity.NoticeEntity;
import com.error404.geulbut.jpa.notice.repository.NoticeRepository;
import com.error404.geulbut.jpa.users.entity.Users;
import com.error404.geulbut.jpa.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UsersRepository usersRepository;

    public Page<NoticeDto> getNotices(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return noticeRepository.findAll(pageRequest).map(this::toDto);
    }
    // 공지 등록
    public NoticeEntity createNotice(NoticeDto dto) {
        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        NoticeEntity notice = NoticeEntity.builder()
                .writer(dto.getWriter())
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(new Date())
                .viewCount(0L)
                .category(dto.getCategory())
                .userId(user)
                .build();

        return noticeRepository.save(notice);
    }

    // 공지 전체 조회
    public List<NoticeDto> findAll() {
        return noticeRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    // 공지 단건 조회
    public NoticeDto findById(Long id) {
        NoticeEntity notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));
        return toDto(notice);
    }

    // Entity → DTO 변환
    private NoticeDto toDto(NoticeEntity entity) {
        return NoticeDto.builder()
                .noticeId(entity.getNoticeId())
                .writer(entity.getWriter())
                .title(entity.getTitle())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .viewCount(entity.getViewCount())
                .category(entity.getCategory())
                .userId(entity.getUserId().getUserId())
                .build();
    }
    // 수정
    public void updateNotice(NoticeDto dto) {
        NoticeEntity entity = noticeRepository.findById(dto.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setUpdatedAt(new Date());
        noticeRepository.save(entity);
    }

    // 삭제
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

    // ✅ 조회수 증가 후 단건 조회
    @Transactional
    public NoticeDto getNoticeAndIncreaseViewCount(Long id) {
        // 1. 조회수 증가
        noticeRepository.increaseViewCount(id);

        // 2. 글 가져오기
        NoticeEntity notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));

        return toDto(notice);
    }


}
