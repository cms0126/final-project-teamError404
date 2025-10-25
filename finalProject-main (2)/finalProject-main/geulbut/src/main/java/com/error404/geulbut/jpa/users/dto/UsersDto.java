package com.error404.geulbut.jpa.users.dto;

import com.error404.geulbut.jpa.users.entity.Users;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UsersDto {

    private String userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String role; // USER, ADMIN, MANAGER
    private LocalDate joinDate;
    private Users.UserStatus status; // ACTIVE, INACTIVE, DELETED, SUSPENDED

    private Long point;
    private String grade;                   // 브론즈/실버/골드

}
