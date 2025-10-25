// TODO : 로그인 요청 DTO

package com.error404.geulbut.jpa.users.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UsersLoginDto {

    private String userId;
    private String password;
}
