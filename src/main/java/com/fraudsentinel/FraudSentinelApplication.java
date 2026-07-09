package com.fraudsentinel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FraudSentinelApplication {

	public static void main(String[] args) {
		SpringApplication.run(FraudSentinelApplication.class, args);
	}
}