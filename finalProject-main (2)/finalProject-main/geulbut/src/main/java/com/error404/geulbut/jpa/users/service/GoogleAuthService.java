package com.error404.geulbut.jpa.users.service;

import com.error404.geulbut.jpa.users.dto.UsersOAuthUpsertDto;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GoogleAuthService implements OAuthProviderService {

    @Override
    public String providerKey() {
        return "google";
    }
    @Override
    public UsersOAuthUpsertDto toUpsertDto(Map<String, Object> attrs) {
//        구글 정보 : sub, email, name, picture...

        String provider   = "google";
        String providerId = String.valueOf(attrs.get("sub"));
        String email      = (String) attrs.get("email");

        // name 기본값: 구글 프로필의 name, 없으면 email 앞부분 사용
        String name       = (String) attrs.getOrDefault("name", email != null ? email.split("@")[0] : null);

        // 구글 API 기본 scope에는 phone, gender, birthday는 없음 → null 처리
        String phone      = null;
        Character gender  = null;       // 남/여 정보 제공 안함
        java.time.LocalDate birthday = null; // 기본 프로필엔 없음

        String imgUrl     = (String) attrs.get("picture");

        return UsersOAuthUpsertDto.builder()
                .provider(provider)
                .providerId(providerId)
                .email(email)
                .name(name)
                .phone(phone)
                .gender(gender)
                .birthday(birthday)
                .imgUrl(imgUrl)
                .build();
    }
}
