package com.error404.geulbut.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "password-recovery")
public class PasswordRecoveryProperties {
    private Duration codeTtl = Duration.ofMinutes(3);
    private Duration resendCooldown = Duration.ofSeconds(60);
    private int maxAttempts = 5;
    private int tempPwLength = 10;

    public Duration getCodeTtl() {return codeTtl; }
    public void setCodeTtl(Duration v) {this.codeTtl = v; }
    public Duration getResendCooldown() { return resendCooldown; }
    public void setResendCooldown(Duration v) {this.resendCooldown = v; }
    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int v) {this.maxAttempts = v; }
    public int getTempPwLength() { return tempPwLength; }
    public void setTempPwLength(int v) {this.tempPwLength = v; }
}
