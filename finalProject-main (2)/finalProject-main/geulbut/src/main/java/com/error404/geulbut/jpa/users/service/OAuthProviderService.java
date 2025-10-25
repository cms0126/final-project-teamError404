package com.error404.geulbut.jpa.users.service;

import com.error404.geulbut.jpa.users.dto.UsersOAuthUpsertDto;

import java.util.Map;

// 3사 각각 속성을 표준 업서트 디티오로 변환하는 역할 3사 공통 인터페이스임
public interface OAuthProviderService {

//    이 서비스가 담당하는 공통 키
    String providerKey();
//    공급자 원본 어트리뷰트 -> 표준 업서트 디티오로 매핑
    UsersOAuthUpsertDto toUpsertDto(Map<String, Object> attributes);
}
