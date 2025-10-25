package com.error404.geulbut.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


// 9월10일 추가 -강대성
//Spring에서 HTTP 요청을 보내고 응답을 받을 수 있는 클래스
//GET, POST, PUT, DELETE 등 HTTP 요청 가능
//예: 외부 API 호출, 다른 서버와 통신할 때 사용

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
