package com.error404.geulbut.jpa.introduction.dto;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class IntroductionDtoTest {

    @Test
    void testGettersAndSetters() {
        IntroductionDto dto = new IntroductionDto();

        dto.setImgUrl("test_img.jpg");
        dto.setTitle("Test Title");
        dto.setName("Test Author");
        dto.setPublishedDate(LocalDate.of(2025, 10, 1));
        dto.setDescription("Test description");
        dto.setBookId(123L);

        assertEquals("test_img.jpg", dto.getImgUrl());
        assertEquals("Test Title", dto.getTitle());
        assertEquals("Test Author", dto.getName());
        assertEquals(LocalDate.of(2025, 10, 1), dto.getPublishedDate());
        assertEquals("Test description", dto.getDescription());
        assertEquals(123L, dto.getBookId());

        // getImageUrl() 테스트
        assertEquals("test_img.jpg", dto.getImageUrl());
    }

    @Test
    void testToString() {
        IntroductionDto dto = new IntroductionDto(
                "test_img.jpg",
                "Test Title",
                "Test Author",
                LocalDate.of(2025, 10, 1),
                "Test description",
                123L
        );

        String str = dto.toString();
        assertTrue(str.contains("test_img.jpg"));
        assertTrue(str.contains("Test Title"));
        assertTrue(str.contains("Test Author"));
        assertTrue(str.contains("2025-10-01"));
        assertTrue(str.contains("Test description"));
        assertTrue(str.contains("123"));
    }
}