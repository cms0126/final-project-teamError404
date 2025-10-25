package com.error404.geulbut.jpa.users.dto;

import lombok.*;

// DTO 클래스
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserMypageDto {
    private String userId;
    private String email;
    private String  joinDate; // 마이페이지에서 읽기전용으로 String(기존 LocalDate)으로 변경
    private String grade;
    private Long point;

    private String userName;
    private String phone;
    private String address;
}
