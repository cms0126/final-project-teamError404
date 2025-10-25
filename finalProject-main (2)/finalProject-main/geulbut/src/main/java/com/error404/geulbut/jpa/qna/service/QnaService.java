package com.error404.geulbut.jpa.qna.service;

import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.qna.dto.QnaDto;
import com.error404.geulbut.jpa.qna.entity.QnaEntity;
import com.error404.geulbut.jpa.qna.repository.QnaRepository;
import com.error404.geulbut.jpa.qnacomment.service.QnaCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;
    private final QnaCommentService qnaCommentService;

    public void saveQna(QnaDto qnaDto) {
        QnaEntity entity = QnaEntity.builder()
                .title(qnaDto.getTitle())
                .qContent(qnaDto.getQContent())  // 여기 값이 제대로 들어가야 함
                .userId(qnaDto.getUserId())
                .qAt(new Date())
                .createdAt(new Date())
                .updatedAt(new Date())
                .viewCount(0L) // 초기 조회수 0
                .build();

        qnaRepository.save(entity);
    }
    public Page<QnaDto> getQnas(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("qnaId").descending());
        return qnaRepository.findAll(pageable)
                .map(entity -> QnaDto.builder()
                        .id(entity.getQnaId())
                        .title(entity.getTitle())
                        .qContent(entity.getQContent())
                        .qAt(entity.getQAt())
                        .aId(entity.getAId())
                        .aContent(entity.getAContent())
                        .aAt(entity.getAAt())
                        .userId(entity.getUserId())
                        .viewCount(entity.getViewCount())

                        .build()
                );
    }
    // 전체 QnA 조회
    public List<QnaEntity> findAll() {
        return qnaRepository.findAll();
    }
    // 특정 QnA 단건 조회 + 댓글 포함
    @Transactional
    public QnaDto getQnaWithComments(Long id) {
        QnaEntity entity = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("글 없음"));

        if (entity.getViewCount() == null) entity.setViewCount(0L);
        entity.setViewCount(entity.getViewCount() + 1);
        qnaRepository.save(entity);

        QnaDto dto = QnaDto.builder()
                .id(entity.getQnaId())
                .title(entity.getTitle())
                .qContent(entity.getQContent())
                .qAt(entity.getQAt())
                .aId(entity.getAId())
                .aContent(entity.getAContent())
                .aAt(entity.getAAt())
                .userId(entity.getUserId())
                .viewCount(entity.getViewCount())
                .build();

        List comments = qnaCommentService.getCommentsByQna(entity.getQnaId());
        dto.setComments(comments);

        return dto;
    }
    // 특정 QnA 조회
    public QnaDto findById(Long id) {
        QnaEntity entity = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 글을 찾을 수 없습니다."));
        return QnaDto.builder()
                .id(entity.getQnaId())
                .title(entity.getTitle())
                .qContent(entity.getQContent())
                .qAt(entity.getQAt())      // Date 타입 그대로
                .aId(entity.getAId())
                .aContent(entity.getAContent())
                .aAt(entity.getAAt())      // Date 타입 그대로
                .userId(entity.getUserId())
                .viewCount(entity.getViewCount())
                .build();
    }
    // 조회수 증가 후 단건 조회
    @Transactional
    public QnaDto getQnaAndIncreaseViewCount(Long id) {
        // 1. 조회수 증가
        QnaEntity entity = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 글을 찾을 수 없습니다."));

        if (entity.getViewCount() == null) {
            entity.setViewCount(0L);
        }
        entity.setViewCount(entity.getViewCount() + 1);

        // 2. 글 저장
        qnaRepository.save(entity);

        // 3. DTO 반환
        return QnaDto.builder()
                .id(entity.getQnaId())
                .title(entity.getTitle())
                .qContent(entity.getQContent())
                .qAt(entity.getQAt())
                .aId(entity.getAId())
                .aContent(entity.getAContent())
                .aAt(entity.getAAt())
                .userId(entity.getUserId())
                .viewCount(entity.getViewCount())
                .build();
    }

    // 특정 Qna 수정
    public void updateQna(QnaDto qnaDto) {
        QnaEntity entity = qnaRepository.findById(qnaDto.getId())
                .orElseThrow(() -> new RuntimeException("글 없음"));
        entity.setTitle(qnaDto.getTitle());
        entity.setQContent(qnaDto.getQContent());
        entity.setUpdatedAt(new Date());
        qnaRepository.save(entity);
    }
    // 삭제
    public void deleteQna(Long id) {
        QnaEntity entity = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("글을 찾을 수 없습니다."));
        qnaRepository.delete(entity);
    }
    // 조회수 상위 10개 (자주 묻는 질문용)
    public List<QnaEntity> getTop10Qna() {
        return qnaRepository.findTop10ByOrderByViewCountDesc();
    }


}


