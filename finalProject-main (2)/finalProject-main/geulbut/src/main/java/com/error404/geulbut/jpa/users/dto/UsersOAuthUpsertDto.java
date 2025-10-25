//  TODO : OAuth 3사(구글,카카오,네이버) 통합 업서트 DTO

package com.error404.geulbut.jpa.users.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UsersOAuthUpsertDto {

    //  OAuth 공통
    private String provider;          // google / naver / kakao
    private String providerId;       // 각 플랫폼의 고유 ID
    private String email;             // 카카오는 null 가능
    private String name;
    private String phone;
    private String imgUrl;

    //  선택 값
    private Character  gender;
    private LocalDate birthday;

    //  Users.userId 생성 규칙
    public String toUserIdKey() {
        return provider + ":" + providerId;
    }


}
