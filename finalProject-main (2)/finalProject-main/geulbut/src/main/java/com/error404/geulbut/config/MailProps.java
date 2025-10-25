package com.error404.geulbut.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.mail")
public class MailProps {
    private String from;
    private String fromName;
    public String getFrom() {return from;}
    public void setFrom(String v) {this.from = v;}
    public String getFromName() {return fromName;}
    public void setFromName(String v) {this.fromName = v;}
}
