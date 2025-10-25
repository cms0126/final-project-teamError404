// com.error404.geulbut.common.notify.EmailSender
package com.error404.geulbut.common.notify;

import com.error404.geulbut.config.MailProps;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;
    private final MailProps mailProps;

    public void sendCode(String to, String code, long ttlMinutes) {
        String subject = "글벗 비밀번호 찾기 인증코드";
        String text = """
                인증코드: %s
                유효시간: %d분
                """.formatted(code, ttlMinutes);
        send(to, subject, text);
    }

    public void sendTempPassword(String to, String temp) {
        String subject = "글벗 임시 비밀번호";
        String text = "임시 비밀번호: %s\n로그인 후 반드시 비밀번호를 변경해 주세요.".formatted(temp);
        send(to, subject, text);
    }

    private void send(String to, String subject, String text) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            msg.setFrom(new InternetAddress(
                    mailProps.getFrom(),
                    mailProps.getFromName(),
                    StandardCharsets.UTF_8.name()
            ));
            msg.setRecipients(Message.RecipientType.TO, to);
            msg.setSubject(subject, StandardCharsets.UTF_8.name());
            msg.setText(text, StandardCharsets.UTF_8.name());
            mailSender.send(msg);
        } catch (Exception e) {
            // 운영에선 로깅/모니터링
            throw new IllegalStateException("메일 전송 실패", e);
        }
    }
}
