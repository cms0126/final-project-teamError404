package com.error404.geulbut.jpa.users.service;

import com.error404.geulbut.jpa.users.dto.UsersOAuthUpsertDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@SuppressWarnings("unchecked")
public class NaverAuthService implements OAuthProviderService {

    @Override
    public String providerKey(){
        return "naver";
    }

    @Override
    public UsersOAuthUpsertDto toUpsertDto(Map<String, Object> attrs) {

        Map<String, Object> res = (Map<String, Object>) attrs.get("response");

        String provider   = "naver";
        String providerId = res == null ? null : (String) res.get("id");
        String email      = res == null ? null : (String) res.get("email");
        String name       = res == null ? null : (String) res.get("name");
        String phone      = res == null ? null : (String) res.get("mobile");
        String imgUrl     = res == null ? null : (String) res.get("profile_image");

//        성별 : "M" or "F" -> Character 변환할거
        Character gender   = null ;
        if (res != null && res.get("gender") != null) {
            String g = ((String) res.get("gender")).trim().toLowerCase();
            gender = g.isEmpty() ? null : g.charAt(0);
        }

//        생년월일 혹은 생일 -> 로컬데이트로 변환 시도한다
        LocalDate birthday = null;
        if (res != null && res.get("birthday") != null && res.get("birthday") != null) {
            try {
                String year = (String) res.get("birthyear");
                String mmdd = (String) res.get("birthday");
                birthday = LocalDate.parse(year + "-" + mmdd);
            } catch (Exception ignored) {}

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
