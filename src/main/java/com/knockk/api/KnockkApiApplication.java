package com.knockk.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.knockk.api.business.ResidentBusinessService;
import com.knockk.api.controller.ResidentController;

@SpringBootApplication
@ComponentScan("com.knockk.api") // to scan for util
public class KnockkApiApplication {

	//private static final Logger logger = LoggerFactory.getLogger(ResidentBusinessService.class);

	public static void main(String[] args) {
		SpringApplication.run(KnockkApiApplication.class, args);
	}

}
