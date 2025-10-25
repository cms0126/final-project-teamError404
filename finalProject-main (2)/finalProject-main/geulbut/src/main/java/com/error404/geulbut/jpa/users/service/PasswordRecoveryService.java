package com.error404.geulbut.jpa.users.service;

import com.error404.geulbut.common.notify.EmailSender;
import com.error404.geulbut.config.PasswordRecoveryProperties;
import com.error404.geulbut.jpa.users.dto.PasswordRecoveryDto.SendEmailCodeRequest;

import com.error404.geulbut.jpa.users.dto.PasswordRecoveryDto.VerifyEmailAndResetRequest;

import com.error404.geulbut.jpa.users.entity.Users;
import com.error404.geulbut.jpa.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Log4j2
public class PasswordRecoveryService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordRecoveryProperties props;
    private final EmailSender emailSender;

    /* ====================== EMAIL 경로 ====================== */

    @Transactional
    public void sendEmailCode(SendEmailCodeRequest req) {
        final String email = normalizeEmail(req.getEmail());

        Users u = usersRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 사용자가 없습니다."));

        if (u.getPwCodeLastSentAt() != null &&
                Duration.between(u.getPwCodeLastSentAt(), now()).compareTo(props.getResendCooldown()) < 0) {
            throw new IllegalArgumentException("코드 재전송은 잠시 후 다시 시도해 주세요.");
        }

        String code = sixDigits();
        u.setPwCode(code);
        u.setPwCodeExpiresAt(now().plus(props.getCodeTtl()));
        u.setPwCodeAttempts(0);
        u.setPwCodeLastSentAt(now());
        usersRepository.save(u);

//       발송
        long ttlMin = props.getCodeTtl().toMinutes();
        emailSender.sendCode(email, code, ttlMin);

    }

    @Transactional
    public String verifyEmailAndReset(VerifyEmailAndResetRequest req) {
        final String email = normalizeEmail(req.getEmail());
        Users u = usersRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("가입 정보를 찾을 수 없습니다."));

        validateAndConsumeCode(u, req.getCode(), props.getMaxAttempts());

        String temp = tempPassword(props.getTempPwLength());
        u.setPasswordTemp(passwordEncoder.encode(temp)); // ← PASSWORD_TEMP에 저장
        u.setTempPwYn("Y");                               // ← 로그인 시 temp 사용
        clearCodeFields(u);                               // 코드 일회성 소모
        usersRepository.save(u);

        return temp; // ← 컨트롤러에서 JSP로 보여주기 위함
    }

    /* ====================== 내부 유틸 ====================== */

    private void validateAndConsumeCode(Users u, String input, int maxAttempts) {
        if (u.getPwCode() == null || u.getPwCodeExpiresAt() == null) {
            throw new IllegalArgumentException("인증코드가 존재하지 않거나 만료되었습니다.");
        }
        if (u.getPwCodeExpiresAt().isBefore(now())) {
            throw new IllegalArgumentException("인증코드가 만료되었습니다. 다시 전송해 주세요.");
        }
        int tries = u.getPwCodeAttempts() == null ? 0 : u.getPwCodeAttempts();
        if (tries >= maxAttempts) {
            throw new IllegalArgumentException("인증 시도 횟수를 초과했습니다. 잠시 후 다시 시도해 주세요.");
        }
        if (!Objects.equals(u.getPwCode(), input)) {
            u.setPwCodeAttempts(tries + 1);
            usersRepository.save(u);
            throw new IllegalArgumentException("인증코드가 올바르지 않습니다.");
        }

        // 성공 → 즉시 무효화(1회성)
        u.setPwCode(null);
        u.setPwCodeExpiresAt(null);
        u.setPwCodeAttempts(0);
        u.setPwCodeLastSentAt(null);
    }

    private void clearCodeFields(Users u) {
        u.setPwCode(null);
        u.setPwCodeExpiresAt(null);
        u.setPwCodeAttempts(0);
        u.setPwCodeLastSentAt(null);
    }

    private String sixDigits() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    private String tempPassword(int len) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }

    private String normalizePhone(String raw) {
        // 하이픈/공백 제거 → 숫자만 저장/비교
        return raw == null ? null : raw.replaceAll("\\D", "");
    }

    private String normalizeEmail(String raw) {
        return raw == null ? null : raw.trim();
    }

    private LocalDateTime now() {
        return LocalDateTime.now();
    }
}
