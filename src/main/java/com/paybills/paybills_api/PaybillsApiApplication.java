package com.paybills.paybills_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.paybills.paybills_api.coredomain.model")
public class PaybillsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaybillsApiApplication.class, args);
	}

}
