package com.dsa360.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Dsa360ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(Dsa360ApiApplication.class, args);
		System.err.println("App started..");
	}
}
