package com.unversity.programm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProgrammApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgrammApplication.class, args);
	}

}
