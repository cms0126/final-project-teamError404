package com.error404.geulbut.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

//        TODO : 각 페이지마다 css폴더와 img폴더를 허용하기 위한 웹 컨피그 처리
//        TODO : 그래서 views의 하위폴더는 다 가능하게 함을 목적
//        TODO : 예를 들어서 WEB-INF/login/login/css 혹은 img 이렇게 경로가 되어있다면
//         공통 css 인 00_common.css는 그대로 <link rel="stylesheet" href="<c:url value='/css/00_common.css'/>"> 하시면 되고
//         공통 css 가 아닌 개별 css는 <link rel="stylesheet" href="<c:url value='/v(별명:views)/하위폴더(users)/하위폴더(login)/css/login.css'/>"> 이렇게 넣으셔야합니다.
//         이미지도 마찬가지입니다. 하드코딩으로 이미지를 넣으신다면 밑에처럼
//         <img src="<c:url value='/v(별명:views)/하위폴더(users)/하위폴더(login)/img/naver-icon.png'/>" alt="예시" class="예시"/>


        registry.addResourceHandler("/v/**")   // ex) /v/users/login/css/login.css
                .addResourceLocations("/WEB-INF/views/")        // 실제위치 /WEB-INF/views/**
                .setCachePeriod(0);

        // webapp/css → /css/**
        registry.addResourceHandler("/css/**")
                .addResourceLocations("/css/")
                .setCachePeriod(0);

        // webapp/js → /js/**
        registry.addResourceHandler("/js/**")
                .addResourceLocations("/js/")
                .setCachePeriod(0);

        // webapp/images → /images/**
        registry.addResourceHandler("/images/**")
                .addResourceLocations("/images/")
                .setCachePeriod(0);

        // ✅ DevTools 요청 무시용
        registry.addResourceHandler("/.well-known/**")
                .addResourceLocations("classpath:/static/.well-known/")
                .setCachePeriod(0);
    }
}
