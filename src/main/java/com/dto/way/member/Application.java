package com.dto.way.member;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

@EnableAsync
@EnableFeignClients
@EnableJpaAuditing // JPA Auditing 활성화
@SpringBootApplication
public class Application {
	public static void main(String[] args) {SpringApplication.run(Application.class, args);}

	@PostConstruct
	public void init() {
		// timezone 설정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		System.out.println("Current TimeZone: " + TimeZone.getDefault().getID());
	}

}