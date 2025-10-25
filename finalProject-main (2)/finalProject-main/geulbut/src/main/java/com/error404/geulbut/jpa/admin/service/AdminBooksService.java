package com.error404.geulbut.jpa.admin.service;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.authors.dto.AuthorsDto;
import com.error404.geulbut.jpa.authors.entity.Authors;
import com.error404.geulbut.jpa.authors.repository.AuthorsRepository;
import com.error404.geulbut.jpa.books.dto.BooksDto;
import com.error404.geulbut.jpa.books.entity.Books;
import com.error404.geulbut.jpa.books.repository.BooksRepository;
import com.error404.geulbut.jpa.categories.dto.CategoriesDto;
import com.error404.geulbut.jpa.categories.entity.Categories;
import com.error404.geulbut.jpa.categories.repository.CategoriesRepository;
import com.error404.geulbut.jpa.publishers.dto.PublishersDto;
import com.error404.geulbut.jpa.publishers.entity.Publishers;
import com.error404.geulbut.jpa.publishers.repository.PublishersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminBooksService {

    private final BooksRepository booksRepository;
    private final AuthorsRepository authorsRepository;
    private final CategoriesRepository categoriesRepository;
    private final PublishersRepository publishersRepository;
    private final MapStruct mapStruct;
    private final ErrorMsg errorMsg;

    // 전체 조회 (페이징)
    public Page<BooksDto> getAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return booksRepository.findAll(pageable)
                .map(book -> {
                    BooksDto dto = mapStruct.toDto(book);

                    // Author/Publisher/Category 이름 세팅
                    setNames(dto, book);

                    // 평점/리뷰 수 세팅 (DB 컬럼 그대로)
                    dto.setRating(book.getRating() != null ? book.getRating() : 0.0);
                    dto.setReviewCount(book.getReviewCount() != null ? book.getReviewCount() : 0L);

                    return dto;
                });
    }


    // 단일 조회
    public BooksDto getBookById(Long bookId) {
        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(errorMsg.getMessage("error.books.notfound")));
        BooksDto dto = mapStruct.toDto(book);

        setNames(dto, book);

        // 평점/리뷰 수 세팅
        dto.setRating(book.getRating() != null ? book.getRating() : 0.0);
        dto.setReviewCount(book.getReviewCount() != null ? book.getReviewCount() : 0L);

        return dto;
    }


    // 도서 등록
    public BooksDto saveBook(BooksDto dto) {
        // null 체크 및 기본값 적용
        if (dto.getDiscountedPrice() == null) {
            dto.setDiscountedPrice(0L);
        }
        if (dto.getOrderCount() == null) {
            dto.setOrderCount(0L);
        }
        if (dto.getWishCount() == null) {
            dto.setWishCount(0L);
        }

        if (dto.getIsbn() != null && booksRepository.existsByIsbn(dto.getIsbn())) {
            throw new IllegalArgumentException(errorMsg.getMessage("error.books.isbn.duplicate"));
        }

        Books book = mapStruct.toEntity(dto);
        setRelations(book);
        Books saved = booksRepository.save(book);

        BooksDto savedDto = mapStruct.toDto(saved);
        setNames(savedDto, saved);
        return savedDto;
    }

    // 도서 수정
    public BooksDto updateBook(BooksDto dto) {
        // null 체크 및 기본값 적용
        if (dto.getDiscountedPrice() == null) {
            dto.setDiscountedPrice(0L);
        }
        if (dto.getOrderCount() == null) {
            dto.setOrderCount(0L);
        }
        if (dto.getWishCount() == null) {
            dto.setWishCount(0L);
        }

        // 기존 도서 조회
        Books existingBook = booksRepository.findById(dto.getBookId())
                .orElseThrow(() -> new IllegalArgumentException(errorMsg.getMessage("error.books.notfound")));

        // DTO에서 넘어온 ID로 임시 엔티티 세팅
        if (dto.getAuthorId() != null) {
            existingBook.setAuthor(new Authors(dto.getAuthorId()));
        }
        if (dto.getPublisherId() != null) {
            existingBook.setPublisher(new Publishers(dto.getPublisherId()));
        }
        if (dto.getCategoryId() != null) {
            existingBook.setCategory(new Categories(dto.getCategoryId()));
        }

        // 나머지 필드 MapStruct로 업데이트
        mapStruct.updateFromDto(dto, existingBook);

        // 실제 DB 엔티티로 관계 세팅 (저장 전)
        setRelations(existingBook);

        // 저장
        Books saved = booksRepository.save(existingBook);

        // DTO로 변환 후 이름 세팅
        BooksDto updatedDto = mapStruct.toDto(saved);
        setNames(updatedDto, saved);

        return updatedDto;
    }


    // 도서 삭제
    @Transactional
    public boolean deleteBook(Long bookId) {
        return booksRepository.findById(bookId).map(book -> {
            // 삭제 플래그
            book.setEsDeleteFlag("Y");
            booksRepository.save(book);
            return true;
        }).orElse(false);
    }

//    검색
    public Page<BooksDto> searchBooks(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Long> bookIdsPage;

        if (keyword == null || keyword.isEmpty()) {
            bookIdsPage = booksRepository.findAll(pageable).map(Books::getBookId);
        } else {
            bookIdsPage = booksRepository.findBookIdsByKeyword(keyword, pageable);
        }

        List<Books> books = booksRepository.findByIdsWithRelations(bookIdsPage.getContent());

        // --- 중복 제거 + 입력 순서 보장 ---
        Map<Long, Books> uniqueMap = new LinkedHashMap<>();
        for (Long id : bookIdsPage.getContent()) {
            books.stream()
                    .filter(b -> b.getBookId().equals(id))
                    .findFirst()
                    .ifPresent(b -> uniqueMap.put(id, b));
        }

        List<BooksDto> dtos = uniqueMap.values().stream()
                .map(book -> {
                    BooksDto dto = mapStruct.toDto(book);
                    setNames(dto, book);
                    if (book.getHashtags() != null) {
                        dto.setHashtags(book.getHashtags().stream()
                                .map(h -> h.getName())
                                .distinct()
                                .toList());
                    }
                    return dto;
                })
                .toList();

        return new PageImpl<>(dtos, pageable, bookIdsPage.getTotalElements());
    }





    // Author, Category, Publisher 관계 세팅
    private void setRelations(Books book) {
        // Author 존재 여부 확인
        if (book.getAuthor() == null || book.getAuthor().getAuthorId() == null) {
            throw new IllegalArgumentException(errorMsg.getMessage("error.books.author.required"));
        } else {
            Authors author = authorsRepository.findById(book.getAuthor().getAuthorId())
                    .orElseThrow(() -> new IllegalArgumentException(errorMsg.getMessage("error.books.author.notfound")));
            book.setAuthor(author);
        }

        // Category 존재 여부 확인
        if (book.getCategory() == null || book.getCategory().getCategoryId() == null) {
            throw new IllegalArgumentException(errorMsg.getMessage("error.books.category.required"));
        } else {
            Categories category = categoriesRepository.findById(book.getCategory().getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException(errorMsg.getMessage("error.books.category.notfound")));
            book.setCategory(category);
        }

        // Publisher 존재 여부 확인
        if (book.getPublisher() == null || book.getPublisher().getPublisherId() == null) {
            throw new IllegalArgumentException(errorMsg.getMessage("error.books.publisher.required"));
        } else {
            Publishers publisher = publishersRepository.findById(book.getPublisher().getPublisherId())
                    .orElseThrow(() -> new IllegalArgumentException(errorMsg.getMessage("error.books.publisher.notfound")));
            book.setPublisher(publisher);
        }
    }

    // DTO에 이름 필드 세팅
    private void setNames(BooksDto dto, Books book) {
        if (book.getAuthor() != null) dto.setAuthorName(book.getAuthor().getName());
        if (book.getPublisher() != null) dto.setPublisherName(book.getPublisher().getName());
        if (book.getCategory() != null) dto.setCategoryName(book.getCategory().getName());
    }

    // 모달용 전체 조회
    public List<AuthorsDto> getAllAuthorsDto() {
        return authorsRepository.findAll(Sort.by("name").ascending())
                .stream().map(mapStruct::toDto).collect(Collectors.toList());
    }

    public List<PublishersDto> getAllPublishersDto() {
        return publishersRepository.findAll(Sort.by("name").ascending())
                .stream().map(mapStruct::toDto).collect(Collectors.toList());
    }

    public List<CategoriesDto> getAllCategoriesDto() {
        return categoriesRepository.findAll(Sort.by("name").ascending())
                .stream().map(mapStruct::toDto).collect(Collectors.toList());
    }

    // 수정 모달용: 단일 도서 + 전체 옵션
//    관계 엔티티 세팅
    public Map<String, Object> getBookAndRelations(Long bookId) {
        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(errorMsg.getMessage("error.books.notfound")));
        BooksDto dto = mapStruct.toDto(book);
//        이름세팅
        setNames(dto, book);

        // ID 세팅 (select option 기본값으로 사용)
        if (book.getAuthor() != null) dto.setAuthorId(book.getAuthor().getAuthorId());
        if (book.getPublisher() != null) dto.setPublisherId(book.getPublisher().getPublisherId());
        if (book.getCategory() != null) dto.setCategoryId(book.getCategory().getCategoryId());

        Map<String, Object> map = new HashMap<>();
        map.put("book", dto);
        map.put("authors", getAllAuthorsDto());
        map.put("publishers", getAllPublishersDto());
        map.put("categories", getAllCategoriesDto());
        return map;
    }
//   해시태그jsp에 사용하기 위해서 전체조회를 추가합니다.
    public List<BooksDto> getAllBooksDto() {
        return booksRepository.findAll(Sort.by("createdAt").descending())
                .stream()
                .map(book -> {
                    BooksDto dto = mapStruct.toDto(book);
                    setNames(dto, book);
                    return dto;
                })
                .collect(Collectors.toList());
    }


}
