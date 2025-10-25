package com.error404.geulbut.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration // 스프링 설정 클래스
public class EnvConfig {

    @PostConstruct // Bean 생성 후 초기화 시 실행
    public void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .directory("src/main/resources") // .env 위치 지정
                .ignoreIfMissing() // 없으면 무시
                .load();

        // .env의 모든 key=value를 시스템 환경 변수로 등록
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}

