package com.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JwtAuthenticationByMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtAuthenticationByMeApplication.class, args);
		
		System.out.println("Running ");
	}

}
