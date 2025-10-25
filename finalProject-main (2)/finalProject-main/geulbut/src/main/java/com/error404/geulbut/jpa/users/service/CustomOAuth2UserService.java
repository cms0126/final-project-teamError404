package com.error404.geulbut.jpa.users.service;

import com.error404.geulbut.common.security.CustomPrincipal;
import com.error404.geulbut.jpa.users.dto.UsersOAuthUpsertDto;
import com.error404.geulbut.jpa.users.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final SocialAuthService socialAuthService;
    private final UsersService usersService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest){
        OAuth2User raw = super.loadUser(userRequest);
        Map<String, Object> attributes = raw.getAttributes();
        String provider = userRequest.getClientRegistration().getRegistrationId(); // google/naver/kakao

        UsersOAuthUpsertDto usersOAuthUpsertDto = socialAuthService.buildUpsertDto(provider, attributes);
        Users saved = usersService.upsertFromOAuth(usersOAuthUpsertDto);

        Map<String, Object> attrs = new LinkedHashMap<>(attributes);
        String displayName = extractDisplayName(provider, attributes);
        attrs.put("displayName", (displayName != null && !displayName.isBlank()) ? displayName : "사용자");
        attrs.putIfAbsent("userId", usersOAuthUpsertDto.toUserIdKey());
        attrs.put("provider", provider.toUpperCase());

        var auths = List.of(new SimpleGrantedAuthority("ROLE_" + saved.getRole()));

        return CustomPrincipal.fromUsers(saved, attrs, auths);

    }

    private String extractDisplayName(String provider, Map<String, Object> attributes){
        switch (provider){
            case "google":
                return asString(attributes.get("name"));

            case "naver": {
                Object resp = attributes.get("response");
                if (resp instanceof Map<?, ?> map) {
                    return asString(map.get("name"));
                }
                return null;
            }
            case "kakao": {
                Object acc = attributes.get("kakao_account");
                if (acc instanceof Map<?, ?> accMap) {
                    Object prof = accMap.get("profile");
                    if (prof instanceof Map<?, ?> profMap) {
                        return asString(profMap.get("nickname"));
                    }
                }
                return null;
            }
            default:
                return null;
        }
    }
    private String asString(Object o){
        return (o == null) ? null : String.valueOf(o);
    }
}
