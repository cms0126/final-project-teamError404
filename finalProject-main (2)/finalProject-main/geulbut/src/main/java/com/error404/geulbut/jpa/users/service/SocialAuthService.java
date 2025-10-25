package com.error404.geulbut.jpa.users.service;

import com.error404.geulbut.jpa.users.dto.UsersOAuthUpsertDto;

import java.util.Map;

//  3사 통합 파사드입니다.
public interface SocialAuthService {
//    레지스트레이션Id + 어트리뷰트스 -> 표준 디티오 변환함
    UsersOAuthUpsertDto buildUpsertDto(String registrationId, Map<String, Object> attributes);

//     표준 디티오를 기반으로 유저즈 테이블 업서트
    void upsertUser(UsersOAuthUpsertDto usersOAuthUpsertDto);

}
