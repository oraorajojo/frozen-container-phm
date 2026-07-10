package com.singsing.frozenapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // JPA Auditing 기능 활성화 -> User.createdAt 필드의 @CreatedDate가 저장 시점에 자동으로 채워지도록 함
public class FrozenapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrozenapiApplication.class, args);
	}

}
