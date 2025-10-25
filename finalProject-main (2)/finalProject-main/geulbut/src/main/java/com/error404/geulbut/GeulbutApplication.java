package com.error404.geulbut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing       // TODO: @CreatedDate 사용하기 위해 붙입니다.
// TODO: JPA 사용 폴더 지정
@EnableJpaRepositories(basePackages = "com.error404.geulbut.jpa")
// TODO: es(엘라스틱서치) 사용 폴더 지정
@EnableElasticsearchRepositories(basePackages = "com.error404.geulbut.es")
public class GeulbutApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeulbutApplication.class, args);
	}

}
