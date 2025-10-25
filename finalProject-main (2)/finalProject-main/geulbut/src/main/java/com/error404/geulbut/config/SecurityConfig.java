package com.error404.geulbut.config;

import com.error404.geulbut.common.security.CustomPrincipal;
import com.error404.geulbut.jpa.users.entity.Users;
import com.error404.geulbut.jpa.users.service.CustomOAuth2UserService;
import com.error404.geulbut.jpa.users.service.SocialAuthService;
import com.error404.geulbut.jpa.users.service.UsersService;
import com.error404.geulbut.security.TempPasswordRedirectSuccessHandler;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

// private final CustomOAuth2UserService customOAuth2UserService;

//    비밀번호 인코더 등록 9/11
    @Bean
    public PasswordEncoder passwordEncoder() {
        var pe = PasswordEncoderFactories.createDelegatingPasswordEncoder(); // 기본 {bcrypt}
        if (pe instanceof DelegatingPasswordEncoder dpe) {
            dpe.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder()); // 레거시 $2a$ 대응
        }
        return pe;
    }

//    개발 중 임시 전체 오픈 스위치(true = 전체허용, false = 원래보안)
    private static final boolean DEV_BYPASS = false;


    @Bean
    SecurityFilterChain filterChain(
                                    HttpSecurity http,
                                    CustomOAuth2UserService customOAuth2UserService,
                                    SocialAuthService socialAuthService,
                                    TempPasswordRedirectSuccessHandler tempPasswordRedirectSuccessHandler,
                                    UsersService usersService
                                    ) throws Exception {
        // 👆 Bean 메서드 파라미터 주입 방식으로 변경

        if (DEV_BYPASS) {
            http
                    .authorizeHttpRequests(auth->auth
                            .requestMatchers("/oauth2/**").permitAll()
                            .anyRequest().permitAll()
                    )
                    .oauth2Login(oauth ->oauth
                            .loginPage("/login")
                    )
                    .csrf(AbstractHttpConfigurer::disable)
                    .headers(headers->headers.frameOptions(frame->frame.sameOrigin()))
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .logoutSuccessUrl("/")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                    );
            return http.build();
        }

        http
                .authorizeHttpRequests(auth->auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR)
                        .permitAll()
                        .dispatcherTypeMatchers(DispatcherType.INCLUDE).permitAll()

                        .requestMatchers(
                                "/", "/ping",
                                "/login", "/login/**", "/loginProc",
                                "/oauth2/**", "/login/oauth2/**", "/oauth2/authorization/**",
                                "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/favicon.ico", "/error",
                                "/v/**", "/notice",
                                "/dustApi","DustWeatherApi","/dust","weather","weatherApi",
                                "admin/css/admin-header.css", "/signup",
                                "/users/check-id","/users/check-email",
                                "/find-id","/find-password",
                                "/find-password/**", "/search",
                                "/find-password/email/code",         // ⬅️ AJAX POST
                                "/find-password/email/verify",
                                "/common/**"

                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form->form
                        .loginPage("/login")
                        .successHandler(tempPasswordRedirectSuccessHandler)
                        .loginProcessingUrl("/loginProc")
                        .usernameParameter("username")
                        .passwordParameter("password")
//                        .defaultSuccessUrl("/", false)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .oauth2Login(oauth->oauth
                        .loginPage("/login")
                        .successHandler(tempPasswordRedirectSuccessHandler)
                        .failureUrl("/login?error")
                        .userInfoEndpoint(userInfo -> userInfo
//                                구글 경로 추가
                                .oidcUserService(googleOidcUserService(socialAuthService, usersService))
//                                네이버/카카오(일반OAuth2) 경로 나눠놨음
                                .userService(customOAuth2UserService)  //  여기서 파라미터로 받은 서비스 사용
                        )
                )
                .logout(logout->logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .csrf(csrf->csrf.disable())
                .headers(h->h.frameOptions(f->f.sameOrigin()));

        return http.build();
    }
    public OAuth2UserService<OidcUserRequest, OidcUser> googleOidcUserService(SocialAuthService socialAuthService,
                                                                              UsersService usersService
    ) {
        OidcUserService delegate = new OidcUserService();
        return userRequest -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);

//            1) 표준 DTO 변환 -> 업서트
            var dto = socialAuthService.buildUpsertDto("google", oidcUser.getClaims());
            Users saved = usersService.upsertFromOAuth(dto);
//              2) 권한
            var auths = List.of(new SimpleGrantedAuthority(saved.getRole()));

//              3) CustomPrincipal 로 감싸서 반환
            return CustomPrincipal.fromUsers(saved, oidcUser.getClaims(), auths);
        };
    }
}
