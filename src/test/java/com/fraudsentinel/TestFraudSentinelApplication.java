package com.fraudsentinel;

import org.springframework.boot.SpringApplication;

public class TestFraudSentinelApplication {

	public static void main(String[] args) {
		SpringApplication.from(FraudSentinelApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
