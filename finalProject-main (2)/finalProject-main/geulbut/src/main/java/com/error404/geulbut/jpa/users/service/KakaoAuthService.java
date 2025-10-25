package com.error404.geulbut.jpa.users.service;

import com.error404.geulbut.jpa.users.dto.UsersOAuthUpsertDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;


@Service
@SuppressWarnings("unchecked")
public class KakaoAuthService implements OAuthProviderService {

    @Override
    public String providerKey(){
        return "kakao";
    }
    @Override
    public UsersOAuthUpsertDto toUpsertDto(Map<String, Object> attrs) {

        String provider = "kakao";
        String providerId = attrs.get("id") == null ? null : String.valueOf(attrs.get("id"));

        Map<String, Object> account = (Map<String, Object>) attrs.get("kakao_account");

        String email = account == null ? null : (String) account.get("email");
        String phone = account == null ? null : (String) account.get("phone_number");
//       프로필
        Map<String, Object> profile = account == null ? null : (Map<String, Object>) account.get("profile");
        String name  = profile != null && profile.get("nickname") != null ? (String) profile.get("nickname") : "카카오사용자";
        String imgUrl = profile != null && profile.get("profile_image_url") != null
                ? (String) profile.get("profile_image_url")
                : (profile != null ? (String) profile.get("thumbnail_image_url") : null);

//        성별
        Character gender = null;
        if (account != null && account.get("gender") != null) {
            String g = ((String)  account.get("gender")).trim().toLowerCase();
            gender = g.startsWith("m") ? 'M' : g.startsWith("f") ? 'F' : null;
        }
//        생년월일 혹은 생일
        LocalDate birthday = null;
        if (account != null && account.get("birthday") != null && account.get("birthyear") != null) {
            try {
                String year = (String) account.get("birthyear");    // 1991
                String mmdd = (String) account.get("birthday");  // 0418
                String month = mmdd.substring(0, 2);
                String day = mmdd.substring(2, 4);
                birthday = LocalDate.parse(year + "-" + month + "-" + day);
            }   catch (Exception ignored) {}
        }

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
