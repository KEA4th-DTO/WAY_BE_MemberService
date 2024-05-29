package com.dto.way.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableFeignClients
@EnableJpaAuditing // JPA Auditing 활성화
@SpringBootApplication
public class Application {
	public static void main(String[] args) {SpringApplication.run(Application.class, args);}

}