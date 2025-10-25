package com.error404.geulbut.jpa.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

public class PasswordRecoveryDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
    public static class SendSmsCodeRequest {
        @NotBlank(message = "휴대폰 번호는 필수입니다.")
        @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대폰 형식이 올바르지 않습니다.")
        private String phone; // 예: 01012345678 (숫자만)
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
    public static class VerifySmsAndResetRequest {
        @NotBlank(message = "휴대폰 번호는 필수입니다.")
        @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대폰 형식이 올바르지 않습니다.")
        private String phone;

        @NotBlank(message = "인증코드는 필수입니다.")
        @Pattern(regexp = "^\\d{6}$", message = "인증코드는 6자리 숫자여야 합니다.")
        private String code;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
    public static class SendEmailCodeRequest {
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
    public static class VerifyEmailAndResetRequest {
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "인증코드는 필수입니다.")
        @Pattern(regexp = "^\\d{6}$", message = "인증코드는 6자리 숫자여야 합니다.")
        private String code;
    }
}
