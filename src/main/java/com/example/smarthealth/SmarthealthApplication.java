package com.example.smarthealth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SmarthealthApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmarthealthApplication.class, args);
	}

}
