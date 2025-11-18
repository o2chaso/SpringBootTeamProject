package com.example.SpringGroupBB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringGroupBbApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGroupBbApplication.class, args);
	}

}
