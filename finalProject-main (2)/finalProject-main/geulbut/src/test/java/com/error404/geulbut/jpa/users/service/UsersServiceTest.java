package com.error404.geulbut.jpa.users.service;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.users.dto.UsersLoginDto;
import com.error404.geulbut.jpa.users.dto.UsersOAuthUpsertDto;
import com.error404.geulbut.jpa.users.dto.UsersSignupDto;
import com.error404.geulbut.jpa.users.entity.Users;
import com.error404.geulbut.jpa.users.repository.UsersRepository;
import org.hibernate.engine.spi.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.error404.geulbut.jpa.users.entity.Users.UserStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock UsersRepository usersRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock MapStruct mapStruct;
    @Mock ErrorMsg errorMsg;

    @InjectMocks UsersService usersService;
    @InjectMocks UsersWithdrawService withdrawService;


    @BeforeEach
    void setUp() {
//        메세지 코드 그대로 반환(간편하게)
        lenient().when(errorMsg.getMessage(Mockito.anyString()))
                .thenAnswer(inv -> inv.getArgument(0));
    }


    @Test
//    회원가입 성공 테스트
    void signup_success() {
        UsersSignupDto usersSignupDto = UsersSignupDto.builder()
                .userId("dkseo").password("123456").name("덕규").build();

        Users mapped = new Users();
                  mapped.setUserId("dkseo");
        given(usersRepository.existsByUserId("dkseo")).willReturn(false);
        given(mapStruct.toEntity(usersSignupDto)).willReturn(mapped);
        given(passwordEncoder.encode("123456")).willReturn("ENC");
        given(usersRepository.save(Mockito.any(Users.class)))
                .willAnswer(inv ->inv.getArgument(0));

        Users saved = usersService.signup(usersSignupDto);

        assertThat(saved.getUserId()).isEqualTo("dkseo");
        assertThat(saved.getPassword()).isEqualTo("ENC");
        assertThat(saved.getRole()).isEqualTo("ROLE_USER");
    }

    // 1) 아이디 중복
    @Test
    void signup_fail_duplicate_userId() {
        UsersSignupDto usersSignupDto = UsersSignupDto.builder()
                .userId("dkseo").password("123456").name("덕규").build();

        given(usersRepository.existsByUserId("dkseo")).willReturn(true);
        given(errorMsg.getMessage("error.user.id.duplicate"))
                .willReturn("이미 사용 중인 아이디입니다.");

        assertThatThrownBy(() -> usersService.signup(usersSignupDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 아이디입니다.");
    }

    // 2) 비밀번호 누락/공백
    @Test
    void signup_fail_missing_password() {
        UsersSignupDto usersSignupDto = UsersSignupDto.builder()
                .userId("dkseo").password(" ").name("덕규").build();

        given(errorMsg.getMessage("error.user.password.required"))
                .willReturn("비밀번호는 필수입니다.");

        assertThatThrownBy(() -> usersService.signup(usersSignupDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 필수입니다.");
    }

    // 3) 요청 자체가 null
    @Test
    void signup_fail_null_request() {
        given(errorMsg.getMessage("error.user.request.null"))
                .willReturn("요청 데이터가 없습니다.");

        assertThatThrownBy(() -> usersService.signup(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("요청 데이터가 없습니다.");
    }
    // 4) 아이디/공백 누락
    @Test
    void signup_fail_missing_userId() {
        UsersSignupDto usersSignupDto = UsersSignupDto.builder()
                .userId(" ").password("123456").name("덕규").build();

        given(errorMsg.getMessage("error.user.id.required"))
                .willReturn("아이디는 필수입니다.");

        assertThatThrownBy(() -> usersService.signup(usersSignupDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디는 필수입니다.");
    }

//  로그인 테스트
//  성공 : 아이디 + 비밀번호 일치
    @Test
    void login_success() {
        UsersLoginDto usersLoginDto = UsersLoginDto.builder()
                .userId("dkseo").password("123456").build();

        Users users = new Users();
        users.setUserId("dkseo");
        users.setPassword("ENC");

        given(usersRepository.findById("dkseo")).willReturn(Optional.of(users));
        given(passwordEncoder.matches("123456", "ENC")).willReturn(true);

        Users result = usersService.login(usersLoginDto);

        assertThat(result.getUserId()).isEqualTo("dkseo");
    }
//    실패1) : 요청 null
    @Test
    void login_fail_null_request() {
        given(errorMsg.getMessage("error.user.request.null"))
                .willReturn("요청 데이터가 없습니다.");
        assertThatThrownBy(()-> usersService.login(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("요청 데이터가 없습니다.");
    }
//    실패2) : 아이디/비번 공백
    @Test
    void login_fail_required_fields(){
        UsersLoginDto usersLoginDto = UsersLoginDto.builder()
                .userId(" ").password(" ").build();
        given(errorMsg.getMessage("error.user.login.required"))
                .willReturn("아이디 / 비밀번호를 입력해주세요.");
        assertThatThrownBy(() -> usersService.login(usersLoginDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디 / 비밀번호를 입력해주세요.");
    }
    // 실패: 아이디 없음
    @Test
    void login_fail_not_found() {
        UsersLoginDto dto = UsersLoginDto.builder()
                .userId("nope").password("x").build();

        given(usersRepository.findById("nope")).willReturn(Optional.empty());
        given(errorMsg.getMessage("error.user.login.notfound"))
                .willReturn("존재하지 않는 아이디입니다.");

        assertThatThrownBy(() -> usersService.login(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 아이디입니다.");
    }

    // 실패: 간편로그인 전용 계정 (비밀번호 없음)
    @Test
    void login_fail_oauth_only() {
        UsersLoginDto dto = UsersLoginDto.builder()
                .userId("social").password("x").build();

        Users u = new Users();
        u.setUserId("social");
        u.setPassword(null); // 소셜 전용

        given(usersRepository.findById("social")).willReturn(Optional.of(u));
        given(errorMsg.getMessage("error.user.login.oauth"))
                .willReturn("간편 로그인 전용 계정입니다.");

        assertThatThrownBy(() -> usersService.login(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("간편 로그인 전용 계정입니다.");
    }

    // 실패: 비밀번호 불일치
    @Test
    void login_fail_password_mismatch() {
        UsersLoginDto dto = UsersLoginDto.builder()
                .userId("dkseo").password("wrong").build();

        Users u = new Users();
        u.setUserId("dkseo");
        u.setPassword("ENC");

        given(usersRepository.findById("dkseo")).willReturn(Optional.of(u));
        given(passwordEncoder.matches("wrong", "ENC")).willReturn(false);
        given(errorMsg.getMessage("error.user.login.password.mismatch"))
                .willReturn("비밀번호가 일치하지 않습니다.");

        assertThatThrownBy(() -> usersService.login(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

// ========================
// OAuth 업서트 테스트
// ========================

    // 성공: 신규 사용자 생성
    @Test
    void upsertFromOAuth_create_new_user() {
        UsersOAuthUpsertDto dto = UsersOAuthUpsertDto.builder()
                .provider("kakao").providerId("123")
                .email("k@k.com").name("카카오유저").imgUrl("http://img").build();

        // 존재하지 않음 → 신규
        given(usersRepository.findById("kakao:123")).willReturn(Optional.empty());
        given(usersRepository.save(Mockito.any(Users.class)))
                .willAnswer(inv -> inv.getArgument(0));

        // MapStruct 부분 업데이트 흉내
        willAnswer(inv -> {
            UsersOAuthUpsertDto d = inv.getArgument(0);
            Users e = inv.getArgument(1);
            if (d.getEmail() != null) e.setEmail(d.getEmail());
            if (d.getName()  != null) e.setName(d.getName());
            if (d.getImgUrl()!= null) e.setImgUrl(d.getImgUrl());
            return null;
        }).given(mapStruct).updateFromOAuth(Mockito.any(), Mockito.any());

        Users saved = usersService.upsertFromOAuth(dto);

        assertThat(saved.getUserId()).isEqualTo("kakao:123");
        assertThat(saved.getRole()).isEqualTo("ROLE_USER");
        assertThat(saved.getGrade()).isEqualTo("BRONZE");
        assertThat(saved.getEmail()).isEqualTo("k@k.com");
        assertThat(saved.getName()).isEqualTo("카카오유저");
        assertThat(saved.getImgUrl()).isEqualTo("http://img");
    }

    // 성공: 기존 사용자 업데이트 (부분 업데이트)
    @Test
    void upsertFromOAuth_update_existing_user() {
        UsersOAuthUpsertDto dto = UsersOAuthUpsertDto.builder()
                .provider("google").providerId("abc")
                .imgUrl("http://new").build();

        Users existing = new Users();
        existing.setUserId("google:abc");
        existing.setRole("ROLE_USER");
        existing.setGrade("BRONZE");
        existing.setImgUrl("http://old");

        given(usersRepository.findById("google:abc")).willReturn(Optional.of(existing));
        given(usersRepository.save(Mockito.any(Users.class)))
                .willAnswer(inv -> inv.getArgument(0));

        willAnswer(inv -> {
            UsersOAuthUpsertDto d = inv.getArgument(0);
            Users e = inv.getArgument(1);
            if (d.getImgUrl() != null) e.setImgUrl(d.getImgUrl());
            return null;
        }).given(mapStruct).updateFromOAuth(Mockito.any(), Mockito.any());

        Users saved = usersService.upsertFromOAuth(dto);

        assertThat(saved.getUserId()).isEqualTo("google:abc");
        assertThat(saved.getImgUrl()).isEqualTo("http://new");
    }

    // 실패: 요청 null
    @Test
    void upsertFromOAuth_fail_null_request() {
        given(errorMsg.getMessage("error.oauth.request.null"))
                .willReturn("요청 데이터가 없습니다.");

        assertThatThrownBy(() -> usersService.upsertFromOAuth(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("요청 데이터가 없습니다.");
    }

    // 실패: provider/providerId 누락
    @Test
    void upsertFromOAuth_fail_missing_provider() {
        UsersOAuthUpsertDto dto = UsersOAuthUpsertDto.builder()
                .provider(" ").providerId(null).build();

        given(errorMsg.getMessage("error.oauth.provider.required"))
                .willReturn("provider/providerId는 필수입니다.");

        assertThatThrownBy(() -> usersService.upsertFromOAuth(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("provider/providerId는 필수입니다.");
    }

//    ===========================================================
//    TODO : 로그인 / 회원가입 관련 성공 혹은 실패 단위 테스트 진행 한 것 9/11 17:00
//    ===========================================================
@Test
void testPasswordMatches() {
    // 1) DB에서 복사해온 해시
    String dbHash = "$2a$10$jDNqWI5wDukZ9SM9WsBYmeysUUC/ie8k1etuVW0CvoZyquiAKB4.."; // 실제 DB의 해시 문자열 붙여넣기

    // 2) 로그인 시도하려는 평문 비번 (네가 새로 설정했던 비번)
    String rawPassword = "1234567890";

    // 3) BCryptPasswordEncoder로 매칭 확인
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    boolean matches = encoder.matches(rawPassword, dbHash);

    System.out.println("매칭 결과: " + matches);

    // 4) AssertJ로도 검증
    assertThat(matches).isTrue(); // 일치해야 한다고 기대
}
    @Test
    void genHashAndPrintSql() {
        // 1) 여기 원하는 새 비번 넣기 (임시로: Test!1234)
        String RAW = "Test!1234";

        // 2) BCrypt로 해시 생성 (앱이 BCrypt 쓰는 게 확실하므로 동일하게)
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder(); // strength 기본 10
        String newHash = enc.encode(RAW);

        // 3) 확인용 매칭 (true 나와야 정상)
        boolean ok = enc.matches(RAW, newHash);

        // 4) 콘솔 출력 (복사-붙여넣기 쉽게)
        System.out.println("=== 새 비번 RAW  ===: " + RAW);
        System.out.println("=== 생성된 HASH ===: " + newHash);
        System.out.println("=== matches?    ===: " + ok);
        System.out.println();
        System.out.println("-- 아래 SQL을 DB에서 실행하세요 --");
        System.out.println("UPDATE users");
        System.out.println("   SET password = '" + newHash + "',");
        System.out.println("       password_temp = NULL,");
        System.out.println("       temp_pw_yn = 'N'");
        System.out.println(" WHERE user_id = 'user002';");
    }

    @Test
    void withdraw_active_user_success() {
        Users u = new Users();
        u.setUserId("user002");
        u.setPassword("ENC");
        u.setStatus(Users.UserStatus.ACTIVE);

        given(usersRepository.findById("user002")).willReturn(Optional.of(u));
        given(usersRepository.save(any(Users.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        withdrawService.withdraw("user002");

        // then
        assertThat(u.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(u.getEmail()).startsWith("withdrawn_user002@");
        assertThat(u.getDeletedAt()).isNotNull();
    }

    @Test
    void withdraw_nonexistent_user_fail() {
        // given
        given(usersRepository.findById("ghost")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> withdrawService.withdraw("ghost"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    void withdraw_already_deleted_user_is_idempotent() {
        // given
        Users u = new Users();
        u.setUserId("user002");
        u.setStatus(UserStatus.DELETED);

        given(usersRepository.findById("user002")).willReturn(Optional.of(u));

        // when
        withdrawService.withdraw("user002");

        // then: 상태 유지 (로그만 남고 아무 일 없음)
        assertThat(u.getStatus()).isEqualTo(UserStatus.DELETED);
    }
}
