package com.error404.geulbut.jpa.users.service;

import com.error404.geulbut.common.ErrorMsg;
import com.error404.geulbut.common.MapStruct;
import com.error404.geulbut.jpa.users.dto.UsersLoginDto;
import com.error404.geulbut.jpa.users.dto.UsersOAuthUpsertDto;
import com.error404.geulbut.jpa.users.dto.UsersSignupDto;
import com.error404.geulbut.jpa.users.entity.Users;
import com.error404.geulbut.jpa.users.repository.UsersRepository;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

//회원가입/로그인 비즈니스 로직용 -강대성
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final MapStruct mapStruct;
    private final ErrorMsg errorMsg;
    private static final Pattern USERID_RE = Pattern.compile("^[a-z0-9]{4,20}$");

    /** totalPurchase 기준으로 grade를 저장 전 항상 동기화 */
    private void ensureGradeConsistent(Users user) {
        long total = (user.getTotalPurchase() == null ? 0L : user.getTotalPurchase());
        String newGrade = computeGrade(total);
        if (newGrade != null && !newGrade.equals(user.getGrade())) {
            user.setGrade(newGrade);
        }
    }

//    아이디 사용 가능 여부 확인
    public boolean isUserIdAvailable(String userId) {
        if (userId == null || userId.isBlank()) return false;
        return !usersRepository.existsByUserId(userId);
    }
//    이메일 사용 가능 여부 확인
    public boolean isEmailAvailable(String email) {
        if (email == null || email.isBlank()) return false;
        return !usersRepository.existsByEmail(email);
    }
//    아이디 찾기 : 이름+이메일-> 마스킹된 userId 반환
    public Optional<FoundIdResult> findUserId(String name, String email) {
        if (name == null || name.isBlank() || email == null || email.isBlank())
            return Optional.empty();
        return usersRepository.findByNameAndEmail(name, email)
                .map(u -> {
                    String masked = maskUserId(u.getUserId());
                    boolean social = isSocialAccount(u);
                    String provider = extractProvider(u);
                    return new FoundIdResult(masked, social, provider);
                });
    }
//    간편 로그인 여부 판단(비번없음 또는 userId에 prefix 존재)
    private boolean isSocialAccount(Users u) {
        String id = u.getUserId();
        return (u.getPassword() == null || u.getPassword().isBlank()) || (id != null && id.contains(":"));
    }
//      힌트 추출 (userId가 "kakao:123" 형태일 때만 확정 가눙)
    private String extractProvider(Users u) {
        String id = u.getUserId();
        if (id == null) return null;
        int p = id.indexOf(':');
        if (p > 0) {
            String prefix = id.substring(0, p).toLowerCase();
            if (prefix.equals("kakao") || prefix.equals("naver") || prefix.equals("google")) return prefix;
        }
        return null;    // 폼 계정과 머지된 케이스는 알 수 없음 -> 화면에서 일반안내
    }


//    비밀번호 재설정 : userId + 이메일 확인 -> 임시 비번 발급/저장
    @Transactional
    public boolean resetPassword (String userId, String email) {
        if (userId == null || userId.isBlank() || email == null || email.isBlank())
            return false;

        return usersRepository.findByUserIdAndEmail(userId, email)
                .map (u ->{
                    String tempPw = generateTempPassword();

                    u.setPasswordTemp(passwordEncoder.encode(tempPw));
                    u.setTempPwYn("Y");
                    u.setPwCode(null);
                    u.setPwCodeAttempts(0);
                    u.setPwCodeExpiresAt(null);
                    u.setPwCodeLastSentAt(null);

                    usersRepository.save(u);
//                    TODO : 메일 발송 서비스에서 tempPw 전송
                    log.info("임시 비밀번호 발급 userId={}, temp={}", userId, tempPw);
                    return true;
                })
                .orElse(false);
    }

    //   TODO :  1. 회원가입
    @Transactional
    public Users signup(UsersSignupDto usersSignupDto) {
        String userId = usersSignupDto.getUserId();
        if (userId == null || !USERID_RE.matcher(userId).matches()){
            throw new IllegalArgumentException("아이디 정책 위반");
        }
//        기본검증
        if (usersSignupDto == null)
            throw new IllegalArgumentException(errorMsg.getMessage("error.user.request.null"));

        if (usersSignupDto.getUserId() == null || usersSignupDto.getUserId().isBlank())
            throw new IllegalArgumentException(errorMsg.getMessage("error.user.id.required"));

        if (usersSignupDto.getPassword() == null || usersSignupDto.getPassword().isBlank())
            throw new IllegalArgumentException(errorMsg.getMessage("error.user.password.required"));
//      아이디중복 확인
        if (usersRepository.existsByUserId(usersSignupDto.getUserId()))
            throw new IllegalArgumentException(errorMsg.getMessage("error.user.id.duplicate"));
//      이메일중복 도 해주면 좋을거같아 추가(이메일이 비어있지 않을 때만 검사)
        String email = usersSignupDto.getEmail();
        if (email != null) {
            email = email.trim().toLowerCase();
            usersSignupDto.setEmail(email);
            if (!email.isBlank() && usersRepository.existsByEmail(email)) {
                throw new IllegalArgumentException(errorMsg.getMessage("error.user.email.duplicate"));
            }
        }
//        전화번호 정규화, 중복체크
        String phone = usersSignupDto.getPhone();
        if (phone != null) {
            phone = phone.replaceAll("[^0-9]", "");  //숫자만
            usersSignupDto.setPhone(phone);
//            폼 회원가입에서는 유니크 보장
            if (!phone.isBlank() && (phone.length() < 10 || phone.length() > 11)) {
                throw new IllegalArgumentException("전화번호 형식을 확인해주세요.");
            }
            if (!phone.isBlank() && usersRepository.existsByPhone(phone)) {
                throw new IllegalArgumentException(errorMsg.getMessage("error.user.phone.duplicate"));
            }
        }

//       동의항목 등을 기본필드 매핑
        Users users = mapStruct.toEntity(usersSignupDto);

//        비밀번호 해시
        users.setPassword(passwordEncoder.encode(usersSignupDto.getPassword()));

//        폼 가입은 무조건 LOCAL
        users.setProvider(Users.AuthProvider.LOCAL);

//        기본값 보정(null 인 경우에만)
        //   "ROLE" IN ('USER','ADMIN','MANAGER') db 제약조건안에 보면 3개중 USER 사용해야함
        if (users.getRole() == null) users.setRole("USER");
        if (users.getGrade() == null) users.setGrade("BRONZE");
        if (users.getPoint() == null) users.setPoint(0L);
        if (users.getTotalPurchase() == null) users.setTotalPurchase(0L);

        if (users.getStatus() == null) users.setStatus(Users.UserStatus.ACTIVE);
        return usersRepository.save(users);
    }

    //   TODO : 2. 로그인(일반)
    public Users login(UsersLoginDto usersLoginDto) {

        log.info("로그인 시도: {}", usersLoginDto);

        if (usersLoginDto == null) {
            throw new IllegalArgumentException(errorMsg.getMessage("error.user.request.null"));
        }
        if (usersLoginDto.getUserId() == null || usersLoginDto.getUserId().isBlank()
                || usersLoginDto.getPassword() == null || usersLoginDto.getPassword().isBlank()) {
            throw new IllegalArgumentException(errorMsg.getMessage("error.user.login.required"));
        }

        // 2) 회원 조회
        Users users = usersRepository.findById(usersLoginDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        errorMsg.getMessage("error.user.login.notfound")));

        // 3) 간편 로그인 계정 여부 체크
        if (users.getPassword() == null || users.getPassword().isBlank()) {
            throw new IllegalArgumentException(errorMsg.getMessage("error.user.login.oauth"));
        }

        // 4) 비밀번호 검증
        if (!passwordEncoder.matches(usersLoginDto.getPassword(), users.getPassword())) {
            throw new IllegalArgumentException(errorMsg.getMessage("error.user.login.password.mismatch"));
        }

        return users;
    }

    //      TODO : 3사 업서트
    @Transactional
    public Users upsertFromOAuth(UsersOAuthUpsertDto usersOAuthUpsertDto) {
        if (usersOAuthUpsertDto == null) {
            throw new IllegalArgumentException(errorMsg.getMessage("error.oauth.request.null"));
        }
        if (usersOAuthUpsertDto.getProvider() == null || usersOAuthUpsertDto.getProvider().isBlank()
                || usersOAuthUpsertDto.getProviderId() == null || usersOAuthUpsertDto.getProviderId().isBlank()) {
            throw new IllegalArgumentException(errorMsg.getMessage("error.oauth.provider.required"));
        }

//      0) 정규화
//        이메일 : trim + 소문자
        String email = nz(usersOAuthUpsertDto.getEmail());
        if (email != null) {
            email = email.trim().toLowerCase();
            usersOAuthUpsertDto.setEmail(email);
        }
//        전화번호 : 숫자만
        String phone = nz(usersOAuthUpsertDto.getPhone());
        if (phone != null) {
            phone = phone.replaceAll("[^0-9]", "");
            usersOAuthUpsertDto.setPhone(phone);
        }
//        성별 : M,F만 유지
        if (usersOAuthUpsertDto.getGender() != null) {
            char g = Character.toUpperCase(usersOAuthUpsertDto.getGender());
            usersOAuthUpsertDto.setGender((g == 'M' || g == 'F') ? g : null);
        }
//        1) 기존계정 존재 여부 매칭 ( 폼 가입 계정과 연결)
//        우선순위 폰 -> 이메일
        Users matched = null;
        if (phone != null && !phone.isBlank()) {
            matched = usersRepository.findByPhone(phone).orElse(null);
        }
        if (matched == null && email != null && !email.isBlank()) {
            matched = usersRepository.findByEmail(email).orElse(null);
        }
//          2) 키 결정 : 매칭되면 그 유저의 userId 아니면 provider:id 로
        final String key = (matched != null) ? matched.getUserId() : usersOAuthUpsertDto.toUserIdKey();
//        final String key = usersOAuthUpsertDto.toUserIdKey();      // 예) "kakao:123456"
        try {
//            업서트
            Users entity = usersRepository.findById(key).orElseGet(() -> {
                Users u = new Users();
                u.setUserId(key);
                applyNewDefaults(u);
                return u;
            });

//          provider 세팅 항상 최신 값 유지
            if (usersOAuthUpsertDto.getProvider() != null) {
                String p = usersOAuthUpsertDto.getProvider().trim().toUpperCase();
                try{
                    entity.setProvider(Users.AuthProvider.valueOf(p));
                } catch (IllegalArgumentException ignore){}
            }

//            부분 업데이트 널 무시 -> 맵 스트럭쳐 사용
            mapStruct.updateFromOAuth(usersOAuthUpsertDto, entity);
            return usersRepository.save(entity);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
//            동시성 최초 로그인 레이스 컨디션 안전장치
            return usersRepository.findById(key).orElseThrow(() -> e);
        }
    }
//    소설 로그인 이용자의 아이디 찾기 결과 표기
    @Getter
    @AllArgsConstructor
    public static class FoundIdResult{
        private final String maskedUserId;  // 화면표시용 마스킹
        private final boolean social;          // 간편로그인 전용인지
        private final String provider;          // 카카오,네이버,구글,널
}
    // 헬퍼
    private void applyNewDefaults(Users users) {
        LocalDateTime now = LocalDateTime.now();

        if (users.getRole() == null) users.setRole("USER");
        if (users.getGrade() == null) users.setGrade("BRONZE");
        if (users.getPoint() == null) users.setPoint(0L);
        if (users.getTotalPurchase() == null) users.setTotalPurchase(0L);
        // 동의 기본값 (CK 제약: 'Y'/'N')
        if (users.getPromoAgree() != 'Y' && users.getPromoAgree() != 'N') users.setPromoAgree('N');
        if (users.getPostNotifyAgree() != 'Y' && users.getPostNotifyAgree() != 'N') users.setPostNotifyAgree('N');

        // 성별 기본값(제약 없으면 'U' 권장)
        if (users.getGender() != 'M' && users.getGender() != 'F') users.setGender('U');

        if (users.getJoinDate() == null) users.setJoinDate(LocalDate.now());
        if (users.getCreatedAt() == null) users.setCreatedAt(now);
        if (users.getUpdatedAt() == null) users.setUpdatedAt(now);
    }

    private static boolean isBlank(String s) {return s == null || s.trim().isEmpty();}
    private static String nz(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
    private String maskUserId(String userId) {
        if (userId == null) return null;
        if (userId.length() <= 3) return "***";
        return userId.substring(0, 2) + "***" + userId.substring(userId.length() - 2);
    }

    private String generateTempPassword() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    // --- ✅ 단순 조회 추가 9/15 승화---
    public Users getUserById(String userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(errorMsg.getMessage("error.user.notfound")));
    }
    // --- 5. 사용자 정보 업데이트 승화 ---
    @Transactional
    public Users updateUser(Users user) {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException(errorMsg.getMessage("error.user.request.null"));
        }
        ensureGradeConsistent(user);
        return usersRepository.save(user);
    }

    // --- 6. 비밀번호 변경 승화 ---
    @Transactional
    public void changePassword(String userId, String currentPw, String newPw, String confirmPw) {
        Users user = getUserById(userId);

//        소셜 로그인 사용자는 불가 하도록
        if (user.getProvider() != Users.AuthProvider.LOCAL) {
            throw new IllegalArgumentException("소셜 로그인(구글/네이버/카카오) 사용자는 비밀번호를 변경할 수 없습니다.");
        }


//        ==================
//        임시비번 관련 로직 추가 : 임시비번으로는 비밀번호 변경 에러가 납니다 : 덕규
//        ==================
        boolean isTemp = "Y".equalsIgnoreCase(user.getTempPwYn())
                                    && user.getPasswordTemp() !=null;
//        1) 현재 비밀번호 매칭(임시비번 로그인 여부에 따라 분기)
        boolean matches = isTemp
                ? passwordEncoder.matches(currentPw, user.getPasswordTemp())
                : passwordEncoder.matches(currentPw, user.getPassword());


        if (!matches) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
//        2) 새 비밀번호 확인
        if (!newPw.equals(confirmPw)) {
            throw new IllegalArgumentException("새 비밀번호가 서로 다릅니다.");
        }
//        3) 정식 비밀번호로 교체
        user.setPassword(passwordEncoder.encode(newPw));

//        4) 임시비번 사용 종료 처리
        if(isTemp) {
            user.setTempPwYn("N");
            user.setPasswordTemp(null);
        }
            usersRepository.save(user);
    }
    // 누적 금액 증가 + 등급 재산정
    @Transactional
    public Users addPurchaseAndRegrade(String userId, long amount) {
        if (amount <= 0) return getUserById(userId);
        Users user = getUserById(userId);
        long cur = (user.getTotalPurchase() == null ? 0L : user.getTotalPurchase());
        long updated = cur + amount;
        user.setTotalPurchase(updated);

        String newGrade = computeGrade(updated);
        if (newGrade != null && !newGrade.equals(user.getGrade())) {
            user.setGrade(newGrade);
        }
        return usersRepository.save(user);
    }
    // 누적금액 감소 + 등급 재산정 (취소/환불)
    @Transactional
    public Users refundAndRegrade(String userId, long amount) {
        if (amount <= 0) return getUserById(userId);

        Users user = getUserById(userId);
        long cur = (user.getTotalPurchase() == null ? 0L : user.getTotalPurchase());
        long updated = cur - amount;
        if (updated < 0) updated = 0;
        user.setTotalPurchase(updated);

        String newGrade = computeGrade(updated);
        if (newGrade != null && !newGrade.equals(user.getGrade())) {
            user.setGrade(newGrade);
        }
        return usersRepository.save(user);
    }
    // 등급 정책 (3단계 // 브론즈-0만원, 실버-10만원, 골드-30만원) 임의대로했어요
    private String computeGrade(long total) {
        if (total >= 300_000L) return "GOLD";
        if (total >= 100_000L) return "SILVER";
        return "BRONZE";
    }
}
