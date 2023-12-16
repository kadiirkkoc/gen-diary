package com.gendiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.gendiary")
public class GenDiaryApplication {
	public static void main(String[] args) {
		SpringApplication.run(GenDiaryApplication.class, args);
	}

}
