package com.lab4.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.lab4.web","com.lab4.application"})
public class Lab4Application {

	public static void main(String[] args) {
		SpringApplication.run(Lab4Application.class, args);
	}

}
