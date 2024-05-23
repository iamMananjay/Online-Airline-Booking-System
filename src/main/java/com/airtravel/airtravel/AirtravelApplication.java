package com.airtravel.airtravel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirtravelApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirtravelApplication.class, args);
	}

}
