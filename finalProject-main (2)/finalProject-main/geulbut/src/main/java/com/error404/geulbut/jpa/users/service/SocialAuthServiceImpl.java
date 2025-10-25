package com.error404.geulbut.jpa.users.service;


import com.error404.geulbut.jpa.users.dto.UsersOAuthUpsertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

//스프링이 같은 타입의 빈들을 **Map<String, OAuthProviderService>**로 주입해줌
//거기서 알맞은 Provider 서비스(Google/Naver/Kakao)를 찾아 toUpsertDto() 호출
//그 DTO를 **UsersService.upsertOAuthUser(dto)**에 넘겨 저장(업서트)

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialAuthServiceImpl implements SocialAuthService {


//     key = 빈 이름(기본: 클래스명 lowerCamelCase)
//     예) googleAuthService, naverAuthService, kakaoAuthService
//     value = 각 공급자 파서 서비스 (OAuthProviderService 구현체)

    private final Map<String, OAuthProviderService> providerServices;

//    DB 업서트 담당 도메인 서비스
    private final UsersService usersService;

    @Override
    public UsersOAuthUpsertDto buildUpsertDto(String registrationId, Map<String, Object> attributes) {
        OAuthProviderService delegate = resolveProvider(registrationId);

        UsersOAuthUpsertDto usersOAuthUpsertDto = delegate.toUpsertDto(attributes);
        if (log.isDebugEnabled()) {
            log.debug("[SocialAuth] registrationId={}, usersOAuthUpsertDto={}", registrationId, usersOAuthUpsertDto);
        }
        return usersOAuthUpsertDto;
    }
    @Override
    public void upsertUser(UsersOAuthUpsertDto usersOAuthUpsertDto) {
        usersService.upsertFromOAuth(usersOAuthUpsertDto);
    }
    private OAuthProviderService resolveProvider(String registrationId) {
        String beanName = registrationId + "AuthService";
        if (providerServices.containsKey(beanName)) {
            return providerServices.get(beanName);
        }
        return providerServices.values().stream()
                .filter(svc->svc.providerKey().equalsIgnoreCase(registrationId))
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException(
                        "지원하지 않는 OAuth Provider: " + registrationId + ", beans=" + providerServices.keySet()));
    }
}
