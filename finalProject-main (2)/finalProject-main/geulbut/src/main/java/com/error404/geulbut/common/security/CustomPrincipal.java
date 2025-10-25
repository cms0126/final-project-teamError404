package com.error404.geulbut.common.security;

import com.error404.geulbut.jpa.users.entity.Users;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
@ToString(exclude = {"password", "attributes", "idToken", "userInfo"})
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 팩토리 메서드만 사용하도록 제한
public class CustomPrincipal implements UserDetails, OAuth2User, OidcUser, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ✅ lowerCamelCase 로 정리
    private final String userId;                     // ex) "user001" or "google:123456"
    private final String name;                       // 전시용 이름
    private final String email;                      // 카카오는 null 가능
    private final String password;                   // 폼 로그인 해시(소셜은 null 가능)
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;    // OAuth2/OIDC 원본 claims/attributes
    private final OidcIdToken idToken;               // OIDC만 존재, 아니면 null
    private final OidcUserInfo userInfo;             // OIDC만 존재, 아니면 null

    // ======================
    // UserDetails
    // ======================
    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override public boolean isAccountNonExpired()  { return true; }
    @Override public boolean isAccountNonLocked()   { return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled()            { return true; }

    // ======================
    // OAuth2User
    // ======================
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * OAuth2User.getName()은 "주로 식별자" 의미로 쓰입니다.
     * 컨트롤러/JSP에서는 전시용 이름은 this.name 을 사용하고,
     * 여기서는 userId 를 반환해 일관되게 식별자로 씁니다.
     */
    @Override
    public String getName() {
        return userId;
    }

    // ======================
    // OidcUser
    // ======================
    @Override
    public Map<String, Object> getClaims() {
        return attributes; // OIDC에서는 claims, 일반 OAuth2에서는 attributes 역할
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }

    // ======================
    // 팩토리 메서드
    // ======================

    /** 일반 OAuth2(네이버/카카오/구글 OAuth2) → OIDC가 아닌 경우 */
    public static CustomPrincipal fromUsers(
            Users user,
            Map<String, Object> attributes,
            Collection<? extends GrantedAuthority> authorities
    ) {
        return new CustomPrincipal(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                authorities != null ? authorities : Collections.emptyList(),
                attributes,
                null,   // idToken 없음
                null    // userInfo 없음
        );
    }

    /** OIDC(구글) → idToken/userInfo 포함해 세션에 올리는 경우 */
    public static CustomPrincipal fromUsersWithOidc(
            Users user,
            Map<String, Object> claims,
            Collection<? extends GrantedAuthority> authorities,
            OidcIdToken idToken,
            OidcUserInfo userInfo
    ) {
        return new CustomPrincipal(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                authorities != null ? authorities : Collections.emptyList(),
                claims,  // OIDC claims를 attributes로 보관
                idToken,
                userInfo
        );
    }

    public boolean isLocal() {
        Object p = attributes != null ? attributes.get("provider") : null;
        if (p instanceof String s) return "LOCAL".equalsIgnoreCase(s);
        return false;
    }
}
