package com.knockk.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// @ComponentScan("com.knockk.api") // to scan for util
public class KnockkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnockkApiApplication.class, args);
	}

}
