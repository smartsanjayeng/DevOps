package com.devops.shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShoppingApplication {

	public static void main(String[] args) {
		System.out.println("Hello gradle..............1");
		SpringApplication.run(ShoppingApplication.class, args);
		System.out.println("Hello gradle..............2");
	}

}
